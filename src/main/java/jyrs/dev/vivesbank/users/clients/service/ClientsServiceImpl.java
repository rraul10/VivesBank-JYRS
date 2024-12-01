package jyrs.dev.vivesbank.users.clients.service;


import jyrs.dev.vivesbank.users.clients.dto.ClientRequestCreate;
import jyrs.dev.vivesbank.users.clients.dto.ClientRequestUpdate;
import jyrs.dev.vivesbank.users.clients.dto.ClientResponse;
import jyrs.dev.vivesbank.users.clients.exceptions.ClientNotFound;
import jyrs.dev.vivesbank.users.clients.mappers.ClientMapper;
import jyrs.dev.vivesbank.users.clients.models.Client;
import jyrs.dev.vivesbank.users.clients.repository.ClientsRepository;
import jyrs.dev.vivesbank.users.clients.storage.service.StorageService;
import jyrs.dev.vivesbank.users.models.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ClientsServiceImpl implements ClientsService {

    private final ClientsRepository repository;
    private final StorageService storageService;
    private final ClientMapper mapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public ClientsServiceImpl(ClientsRepository repository, StorageService storageService, ClientMapper mapper, RedisTemplate<String, Object> redisTemplate) {
        this.repository = repository;
        this.storageService = storageService;
        this.mapper = mapper;
        this.redisTemplate = redisTemplate;
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

/*
    @Override
    public List<ClientResponse> getAllIsDeleted(Boolean isDeleted) {
        var lista = repository.getAllByIsDeleted(isDeleted);

        return lista.stream().map(mapper::toResponse).toList();
    }

 */


    @Override
    public ClientResponse getById(Long id) {
        String redisKey = "client:id:" + id;
        ClientResponse cachedClient = (ClientResponse) redisTemplate.opsForValue().get(redisKey);

        if (cachedClient != null) {
            log.info("Cliente obtenido desde caché de Redis");
            return cachedClient;
        }

        var cliente = repository.findById(id).orElseThrow(() -> new ClientNotFound(id.toString()));

        cachedClient = mapper.toResponse(cliente);
        redisTemplate.opsForValue().set(redisKey, cachedClient, 10, TimeUnit.MINUTES);

        log.info("Cliente obtenido desde la base de datos y almacenado en caché Redis");
        return cachedClient;
    }

/*
    @Override
    public ClientResponse getByUsername(String username) {
        var cliente = repository.getByUsername(username).orElseThrow(()->new ClientNotFound(username));
        return mapper.toResponse(cliente);
    }

 */

    @Override
    public ClientResponse getByDni(String dni) {
        String redisKey = "client:dni:" + dni;
        ClientResponse cachedClient = (ClientResponse) redisTemplate.opsForValue().get(redisKey);

        if (cachedClient != null) {
            log.info("Cliente con DNI {} obtenido desde caché de Redis", dni);
            return cachedClient;
        }

        var cliente = repository.getByDni(dni).orElseThrow(() -> new ClientNotFound(dni));

        cachedClient = mapper.toResponse(cliente);
        redisTemplate.opsForValue().set(redisKey, cachedClient, 10, TimeUnit.MINUTES);

        log.info("Cliente con DNI {} obtenido desde la base de datos y almacenado en caché Redis", dni);
        return cachedClient;
    }


    @Override
    public ClientResponse create(ClientRequestCreate clienteRequest, MultipartFile image) {
        var cliente = mapper.toClientCreate(clienteRequest);

        var tipo = "DNI-" + cliente.getEmail();
        String imageStored = storageService.store(image, tipo);
        String imageUrl = imageStored;

        cliente.setFotoDni(imageUrl);

        var clienteGuardado = repository.save(cliente);

        return mapper.toResponse(clienteGuardado);
    }

    @Override
    public ClientResponse update(Long id, ClientRequestUpdate clienteRequest) {
        var cliente = mapper.toClientUpdate(clienteRequest);
        var res = repository.findById(id).orElseThrow(() -> new ClientNotFound(id.toString()));

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
    public ClientResponse updateDni(Long id, MultipartFile fotoDni) {
        var cliente = repository.findById(id).orElseThrow(() -> new ClientNotFound(id.toString()));

        var email = cliente.getEmail();
        var tipo = "DNI-" + email;
        String imageStored = storageService.store(fotoDni, tipo);
        storageService.delete(cliente.getFotoDni());

        cliente.setFotoDni(imageStored);

        var clienteActualizado = repository.save(cliente);

        return mapper.toResponse(clienteActualizado);
    }

    @Override
    public ClientResponse updatePerfil(Long id, MultipartFile fotoPerfil) {
        var cliente = repository.findById(id).orElseThrow(() -> new ClientNotFound(id.toString()));
        User user = cliente.getUser();
        var email = user.getUsername();
        var tipo = "PROFILE-" + email;
        String imageStored = storageService.store(fotoPerfil, tipo);
        storageService.delete(user.getUsername());

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

        cliente.setUser(user);

        repository.save(cliente);
    }
}
