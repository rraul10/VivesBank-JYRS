package jyrs.dev.vivesbank.users.clients.service;

import jyrs.dev.vivesbank.users.clients.dto.ClientRequest;
import jyrs.dev.vivesbank.users.clients.dto.ClientResponse;
import jyrs.dev.vivesbank.users.clients.models.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ClientsService {
    Page<ClientResponse> getAll(Optional<String> nombre, Optional<String> apellidos, Optional<String> ciudad,Optional<String> provincia, Pageable pageable);

    ClientResponse getById(Long id);

    ClientResponse getByUsername(String username);

    ClientResponse getByDni(String dni);

    ClientResponse create(ClientRequest clienteRequest, MultipartFile image);

    ClientResponse update(Long id, ClientRequest clienteRequest,MultipartFile image);

    List<ClientResponse> getAllIsDeleted(Boolean isDeleted);

    void delete(Long id);


}
