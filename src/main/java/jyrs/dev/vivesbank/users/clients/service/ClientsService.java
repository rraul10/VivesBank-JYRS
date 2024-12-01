package jyrs.dev.vivesbank.users.clients.service;

import jyrs.dev.vivesbank.users.clients.dto.ClientRequestCreate;
import jyrs.dev.vivesbank.users.clients.dto.ClientRequestUpdate;
import jyrs.dev.vivesbank.users.clients.dto.ClientResponse;
import jyrs.dev.vivesbank.users.clients.models.Client;
import jyrs.dev.vivesbank.users.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Optional;

public interface ClientsService {
    Page<ClientResponse> getAll(Optional<String> nombre, Optional<String> apellidos, Optional<String> ciudad, Optional<String> provincia, Pageable pageable);
    ClientResponse getById(Long id);
    ClientResponse getByDni(String dni);
    ClientResponse getByUserGuuid(String dni);
    ClientResponse create(ClientRequestCreate clienteRequest, MultipartFile image, User user);
    ClientResponse updateMe(String id, ClientRequestUpdate clienteRequest);
    ClientResponse updateMeDni(String id, MultipartFile fotoDni);
    ClientResponse updateMePerfil(String id, MultipartFile fotoPerfil);
    void delete(Long id);
    void deleteMe(String id);
    void exportJson(File file, List<Client> clients);
    void importJson(File file);


}
