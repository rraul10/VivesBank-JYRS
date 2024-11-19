package jyrs.dev.vivesbank.users.clients.service;


import jyrs.dev.vivesbank.users.clients.dto.ClientRequest;
import jyrs.dev.vivesbank.users.clients.dto.ClientResponse;
import jyrs.dev.vivesbank.users.clients.exceptions.ClientNotFound;
import jyrs.dev.vivesbank.users.clients.mappers.ClientMapper;
import jyrs.dev.vivesbank.users.clients.models.Client;
import jyrs.dev.vivesbank.users.clients.repository.ClientsRepository;
import jyrs.dev.vivesbank.users.clients.storage.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ClientsServiceImpl implements ClientsService{

    private final ClientsRepository repository;
    private final StorageService storageService;
    private final ClientMapper mapper;

    public ClientsServiceImpl(ClientsRepository repository, StorageService storageService, ClientMapper mapper) {
        this.repository = repository;
        this.storageService = storageService;
        this.mapper = mapper;
    }

    @Override
    public Page<ClientResponse> getAll(Optional<String> nombre, Optional<String> apellidos, Optional<String> ciudad, Optional<String> provincia, Pageable pageable) {
        Specification<Client> specNombre = (root, query, criteriaBuilder) ->
                nombre.map(n -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + n.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Client> specApellido = (root, query, criteriaBuilder) ->
                apellidos.map(a -> criteriaBuilder.like(root.get("apellidos"), a))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Client> specCiudad = (root, query, criteriaBuilder) ->
                ciudad.map(c -> criteriaBuilder.like(criteriaBuilder.lower(root.get("direccion").get("ciudad")), "%" + c.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Client> specProvincia = (root, query, criteriaBuilder) ->
                ciudad.map(c -> criteriaBuilder.like(criteriaBuilder.lower(root.get("direccion").get("provincia")), "%" + c.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));



        Specification<Client> criterio = Specification.where(specNombre)
                .and(specApellido)
                .and(specCiudad)
                .and(specProvincia);

        var page = repository.findAll(criterio, pageable);
        return page.map(mapper::toResponse);
    }

    @Override
    public List<ClientResponse> getAllIsDeleted(Boolean isDeleted) {
        var lista = repository.getAllByIsDeleted(isDeleted);

        return lista.stream().map(mapper::toResponse).toList();
    }



    @Override
    public ClientResponse getById(Long id) {
        var cliente = repository.findById(id).orElseThrow(()->new ClientNotFound(id.toString()));
        return mapper.toResponse(cliente);
    }

    @Override
    public ClientResponse getByUsername(String username) {
        var cliente = repository.getByUsername(username).orElseThrow(()->new ClientNotFound(username));
        return mapper.toResponse(cliente);
    }

    @Override
    public ClientResponse getByDni(String dni) {
        var cliente = repository.getByDni(dni).orElseThrow(()->new ClientNotFound(dni));
        return mapper.toResponse(cliente);
    }

    @Override
    public ClientResponse create(ClientRequest clienteRequest, MultipartFile image) {
        var cliente= mapper.toClient(clienteRequest);

        var tipo = "DNI-"+cliente.getUsername();
        String imageStored = storageService.store(image,tipo);
        String imageUrl = imageStored;

        cliente.setFotoDni(imageUrl);

        var clienteGuardado= repository.save(cliente);

        return mapper.toResponse(clienteGuardado);
    }

    @Override
    public ClientResponse update(Long id, ClientRequest clienteRequest,MultipartFile image) {
        var cliente = mapper.toClient(clienteRequest);

        var res = repository.findById(id).orElseThrow(() -> new ClientNotFound(id.toString()));

        res.setNombre(cliente.getNombre());
        res.setApellidos(cliente.getApellidos());
        res.setEmail(cliente.getEmail());
        res.setDireccion(cliente.getDireccion());
        res.setNumTelefono(cliente.getNumTelefono());
        res.setUpdatedAt(LocalDateTime.now());

        var tipo = "DNI-"+cliente.getUsername();
        String imageStored = storageService.store(image,tipo);
        res.setFotoDni(imageStored);


        var clienteActualizado= repository.save(res);


        return mapper.toResponse(clienteActualizado);
    }





    @Override
    public void delete(Long id) {
        var cliente = repository.findById(id).orElseThrow(() -> new ClientNotFound(id.toString()));

        repository.deleteById(id);
        storageService.delete(cliente.getFotoDni());

    }
}
