package jyrs.dev.vivesbank.users.clients.service;


import jyrs.dev.vivesbank.products.bankAccounts.dto.BankAccountResponse;
import jyrs.dev.vivesbank.products.bankAccounts.mappers.BankAccountMapper;
import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import jyrs.dev.vivesbank.products.bankAccounts.services.BankAccountService;
import jyrs.dev.vivesbank.users.clients.dto.ClientRequestCreate;
import jyrs.dev.vivesbank.users.clients.dto.ClientRequestUpdate;
import jyrs.dev.vivesbank.users.clients.dto.ClientResponse;
import jyrs.dev.vivesbank.users.clients.exceptions.ClientNotFound;
import jyrs.dev.vivesbank.users.clients.exceptions.ClienteExists;
import jyrs.dev.vivesbank.users.clients.mappers.ClientMapper;
import jyrs.dev.vivesbank.users.clients.models.Client;
import jyrs.dev.vivesbank.users.clients.repository.ClientsRepository;
import jyrs.dev.vivesbank.users.clients.service.storage.ClientStorage;
import jyrs.dev.vivesbank.users.clients.storage.service.StorageService;
import jyrs.dev.vivesbank.users.models.Role;
import jyrs.dev.vivesbank.users.models.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ClientsServiceImpl implements ClientsService {

    private final ClientsRepository repository;
    private final StorageService storageService;
    private final ClientMapper mapper;
    private final ClientStorage storage;
    private final BankAccountService bankAccountService;
    private final RedisTemplate<String, Client> redisTemplate;
    @Autowired

    public ClientsServiceImpl(ClientsRepository repository, RedisTemplate<String, Client> redisTemplate, StorageService storageService, ClientMapper mapper, ClientStorage storage, BankAccountService bankAccountService) {
        this.repository = repository;
        this.storageService = storageService;
        this.mapper = mapper;
        this.storage = storage;
        this.redisTemplate = redisTemplate;
        this.bankAccountService = bankAccountService;
    }

    @Override
    public Page<ClientResponse> getAll(Optional<String> nombre, Optional<String> apellidos,
                                       Optional<String> ciudad, Optional<String> provincia, Pageable pageable) {
        Specification<Client> specNombre = (root, query, criteriaBuilder) ->
                nombre.map(n -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + n.toLowerCase() + "%"))
                        .orElse(null);

        Specification<Client> specApellido = (root, query, criteriaBuilder) ->
                apellidos.map(a -> criteriaBuilder.like(criteriaBuilder.lower(root.get("apellidos")), "%" + a.toLowerCase() + "%"))
                        .orElse(null);

        Specification<Client> specCiudad = (root, query, criteriaBuilder) ->
                ciudad.map(c -> criteriaBuilder.like(criteriaBuilder.lower(root.get("direccion").get("ciudad")), "%" + c.toLowerCase() + "%"))
                        .orElse(null);

        Specification<Client> specProvincia = (root, query, criteriaBuilder) ->
                provincia.map(p -> criteriaBuilder.like(criteriaBuilder.lower(root.get("direccion").get("provincia")), "%" + p.toLowerCase() + "%"))
                        .orElse(null);

        Specification<Client> criterio = Specification.where(specNombre)
                .and(specApellido)
                .and(specCiudad)
                .and(specProvincia);

        var page = repository.findAll(criterio, pageable);
        return page.map(mapper::toResponse);
    }


    @Override
    public ClientResponse getById(Long id) {
        String redisKey = "client:id:" + id;
        Client cachedClient = redisTemplate.opsForValue().get(redisKey);

        if (cachedClient != null) {
            log.info("Cliente obtenido desde caché de Redis");
            return mapper.toResponse(cachedClient);
        }

        var cliente = repository.findById(id).orElseThrow(() -> new ClientNotFound(id.toString()));

        redisTemplate.opsForValue().set(redisKey, cliente, 10, TimeUnit.MINUTES);

        log.info("Cliente obtenido desde la base de datos y almacenado en caché Redis");
        return mapper.toResponse(cliente);
    }

    @Override
    public ClientResponse getByUserGuuid(String user) {
        var cliente = repository.getByUser_Guuid(user).orElseThrow(() -> new ClientNotFound(user));
        return mapper.toResponse(cliente);
    }

    @Override
    public ClientResponse getByDni(String dni) {
        String redisKey = "client:dni:" + dni;
        Client cachedClient = redisTemplate.opsForValue().get(redisKey);

        if (cachedClient != null) {
            log.info("Cliente con DNI {} obtenido desde caché de Redis", dni);
            return mapper.toResponse(cachedClient);
        }

        var cliente = repository.getByDni(dni).orElseThrow(() -> new ClientNotFound(dni));


        redisTemplate.opsForValue().set(redisKey, cliente, 10, TimeUnit.MINUTES);

        log.info("Cliente con DNI {} obtenido desde la base de datos y almacenado en caché Redis", dni);
        return mapper.toResponse(cliente);
    }


    @Override
    public ClientResponse create(ClientRequestCreate clienteRequest, MultipartFile image,User user) {
        log.info("  Haciendo mapeo {}", clienteRequest);
        var cliente = mapper.fromClientCreate(clienteRequest);


        repository.getByDni(cliente.getDni()).ifPresent(existingClient -> {
            throw new ClienteExists(cliente.getDni());
        });


        var tipo = "DNI-" + cliente.getEmail();
        String imageUrl = storageService.store(image, tipo);

        cliente.setFotoDni(imageUrl);

        var roles = new HashSet<Role>(user.getRoles());
        roles.add(Role.CLIENT);
        user.setRoles(roles);
        cliente.setUser(user);

        var clienteGuardado = repository.save(cliente);

        return mapper.toResponse(clienteGuardado);
    }

    @Override
    public ClientResponse updateMe(String id, ClientRequestUpdate clienteRequest) {
        var cliente = mapper.fromClientUpdate(clienteRequest);

        var res = repository.getByUser_Guuid(id).orElseThrow(() -> new ClientNotFound(id));
        res.setNombre(cliente.getNombre() != null ? cliente.getNombre() : res.getNombre());
        res.setApellidos(cliente.getApellidos() != null ? cliente.getApellidos() : res.getApellidos());
        res.setEmail(cliente.getEmail() != null ? cliente.getEmail() : res.getEmail());
        res.setDireccion(cliente.getDireccion() != null ? cliente.getDireccion() : res.getDireccion());
        res.setNumTelefono(cliente.getNumTelefono() != null ? cliente.getNombre() : res.getNombre());

        User user = res.getUser();
        user.setUsername(clienteRequest.getEmail() != null ? cliente.getEmail() : user.getUsername());
        user.setPassword(clienteRequest.getPassword() != null ? clienteRequest.getPassword() : user.getPassword());
        res.setUser(user);
      
        redisTemplate.delete("client:id:" + id);

        var clienteActualizado = repository.save(res);
        return mapper.toResponse(clienteActualizado);
    }
      
    @Override
    public ClientResponse updateMeDni(String id, MultipartFile fotoDni) {
        var cliente = repository.getByUser_Guuid(id).orElseThrow(() -> new ClientNotFound(id.toString()));
        String fotoVieja = cliente.getFotoDni();
        var email = cliente.getEmail();
        var tipo = "DNI-" + email;
        String imageStored = storageService.store(fotoDni, tipo);
        storageService.delete(fotoVieja);

        cliente.setFotoDni(imageStored);

        var clienteActualizado = repository.save(cliente);

        return mapper.toResponse(clienteActualizado);
    }


    @Override
    public ClientResponse updateMePerfil(String id, MultipartFile fotoPerfil) {
        var cliente = repository.getByUser_Guuid(id).orElseThrow(() -> new ClientNotFound(id));

        User user = cliente.getUser();
        String fotoVieja = user.getFotoPerfil();
        var email = user.getUsername();
        var tipo = "PROFILE-" + email;
        String imageStored = storageService.store(fotoPerfil, tipo);

        storageService.delete(fotoVieja);

        user.setFotoPerfil(imageStored);

        cliente.setUser(user);
        var clienteActualizado = repository.save(cliente);

        return mapper.toResponse(clienteActualizado);
    }


    @Override
    public void delete(Long id) {
        var cliente = repository.findById(id).orElseThrow(() -> new ClientNotFound(id.toString()));

        repository.deleteById(id);
        storageService.delete(cliente.getFotoDni());

    }

    public void deleteLog(Long id) {
        var cliente = repository.findById(id).orElseThrow(() -> new ClientNotFound(id.toString()));

        User user = cliente.getUser();

        user.setIsDeleted(true);

        var roles = new HashSet<Role>();
        user.setRoles(roles);

        cliente.setUser(user);

        cliente.setUser(user);

        repository.save(cliente);
    }


    @Override
    public void deleteMe(String id) {
        var cliente = repository.getByUser_Guuid(id).orElseThrow(() -> new ClientNotFound(id));

        repository.deleteById(cliente.getId());
        storageService.delete(cliente.getFotoDni());

    }
    public void deleteMeLog(String id) {
        var cliente = repository.getByUser_Guuid(id).orElseThrow(() -> new ClientNotFound(id));

        User user = cliente.getUser();

        user.setIsDeleted(true);

        var roles = new HashSet<Role>();
        user.setRoles(roles);

        cliente.setUser(user);

        repository.save(cliente);
    }

    @Override
    public void exportJson(File file, List<Client> clients) {
        log.info("Exportando clients a JSON");

        storage.exportJson(file,clients);
    }

    @Override
    public void importJson(File file) {
        log.info("Importando clients desde JSON");

        List<Client> clients= storage.importJson(file);

        repository.saveAll(clients);
    }


}
