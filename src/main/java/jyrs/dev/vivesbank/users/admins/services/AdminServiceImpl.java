package jyrs.dev.vivesbank.users.admins.services;

import jyrs.dev.vivesbank.users.admins.dto.AdminRequestDto;
import jyrs.dev.vivesbank.users.admins.dto.AdminUpdateRequest;
import jyrs.dev.vivesbank.users.admins.exceptions.AdminExceptions;
import jyrs.dev.vivesbank.users.admins.repository.AdminRepository;
import jyrs.dev.vivesbank.users.admins.dto.AdminResponseDto;
import jyrs.dev.vivesbank.users.admins.mappers.AdminMappers;
import jyrs.dev.vivesbank.users.admins.storage.AdminStorage;
import jyrs.dev.vivesbank.users.models.Admin;
import jyrs.dev.vivesbank.users.models.Role;
import jyrs.dev.vivesbank.users.models.User;
import jyrs.dev.vivesbank.users.users.exceptions.UserExceptions;
import jyrs.dev.vivesbank.users.users.repositories.UsersRepository;
import jyrs.dev.vivesbank.users.users.services.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Servicio encargado de la gestión de administradores.
 */
@Service
@Slf4j
public class AdminServiceImpl implements AdminService {
    /**
     * Repositorio de administradores
     */
    private final AdminRepository adminRepository;
    /**
     * Storage para json de administradores
     */
    private final AdminStorage adminStorage;
    /**
     * Mappers de administradores
     */
    private final AdminMappers adminMappers;
    /**
     * Repositorio de usuarios
     */
    private final UsersRepository usersRepository;
    @Autowired

    public AdminServiceImpl(AdminRepository adminRepository, AdminStorage adminStorage, AdminMappers adminMappers, UsersRepository usersRepository) {
        this.adminRepository = adminRepository;
        this.adminStorage = adminStorage;
        this.adminMappers = adminMappers;
        this.usersRepository = usersRepository;
    }

    /**
     * Devuelve los administradores paginados en base a los parametros de username y isDeleted.
     * @param username filtro por el nombre de usuario
     * @param isDeleted filtro por isDelted del usuario
     * @param pageable página
     * @return los admins encontrados paginados
     */

    @Override
    public Page<AdminResponseDto> getAllAdmins(Optional<String> username, Optional<Boolean> isDeleted, Pageable pageable) {
        log.info("Getting all admins");

        Specification<Admin> specUserName = ((root, query, criteriaBuilder) ->
                username.map(u -> criteriaBuilder.like(criteriaBuilder.lower(root.get("user").get("username")), "%" + u.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true))));

        Specification<Admin> specIsDeleted = ((root, query, criteriaBuilder) ->
                isDeleted.map(d -> criteriaBuilder.equal(root.get("user").get("isDeleted"), d))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true))));

        Specification<Admin> criterio = Specification.where(specUserName).and(specIsDeleted);

        return adminRepository.findAll(criterio, pageable).map(adminMappers::fromAdminToResponse);
    }

    /**
     * Obtiene el admin por su guuid
     * @param id guuid
     * @return admin o excepcion de adminNotFound en caso de que no se encuentra.
     */
    @Override
    public AdminResponseDto getAdminByGuuid(String id) {
        log.info("Buscando admin con guuid: " + id);
        var res = adminMappers.fromAdminToResponse(adminRepository.findByGuuid(id));
        if (res == null){
            throw new AdminExceptions.AdminNotFound("No se ha encontrado admin con guuid: " + id);
        }
        return res;
    }

    /**
     * Crea un administrador y lo guarda en la base de datos a partir de un usuario existente.
     * @param request la request con los datos para guardar al nuevo administrador.
     * @return el adminResponse con el admin guardado
     * @throws AdminExceptions.AdminAlreadyExists en caso de que el administrador que quieres guardar ya exista.
     */
    @Override
    public AdminResponseDto saveAdmin(AdminRequestDto request) throws AdminExceptions.AdminAlreadyExists {
        log.info("Añadiendo nuevo admin al sistema" + request);
        var admin = adminMappers.fromAdminDto(request);
        var adminForSave = adminRepository.findByGuuid(admin.getGuuid());
        if(adminForSave != null){
            throw new AdminExceptions.AdminAlreadyExists("Ya existe un admin con el mismo guuid");
        }
        var user = usersRepository.findByGuuid(request.getGuuid());
        if(user == null){
            throw new UserExceptions.UserNotFound("No se ha encontrado usuario con el guuid: " + request.getGuuid());
        }
        Set<Role> updatedRoles = new HashSet<>(user.getRoles());
        updatedRoles.add(Role.ADMIN);
        user.setRoles(updatedRoles);
        admin.setUser(user);
        admin.setGuuid(user.getGuuid());
        var adminGuardado = adminRepository.save(admin);
        return adminMappers.fromAdminToResponse(adminGuardado);
    }

    /**
     * Actualiza un administrador del sistema en base a su id.
     * @param id guuid del usuario
     * @param user request con los datos para actualizar
     * @return el admin actualizado o adminNotFound en caso de que no exista admin por ese guuid.
     */
    @Override
    public AdminResponseDto updateAdmin(String id, AdminUpdateRequest user) {
        log.info("Buscando administrador en el sistema: " + user);
        var adminToUpdate = adminRepository.findByGuuid(id);
        if(adminToUpdate == null){
            throw new AdminExceptions.AdminNotFound("No se ha encontrado admin con guuid: " + id);
        }
        var userToUpdate = adminToUpdate.getUser();
        userToUpdate.setUsername(user.getUsername());
        userToUpdate.setFotoPerfil(user.getFotoPerfil());
        adminToUpdate.setUser(userToUpdate);
        adminRepository.save(adminToUpdate);
        return adminMappers.fromAdminToResponse(adminToUpdate);
    }

    /**
     * Borra un administrador por su id
     * @param id guuid
     * @throws AdminExceptions.AdminCannotBeDeleted en caso de que se quiera borrar el admin principal.
     */
    @Override
    public void deleteAdmin(String id) throws AdminExceptions.AdminCannotBeDeleted {
        log.info("Eliminando admin con guuid: " + id);
        var adminToDelete = adminRepository.findByGuuid(id);
        if(adminToDelete == null){
            throw new AdminExceptions.AdminNotFound("No se ha encontrado admin con guuid: " + id);
        }
        log.info(String.valueOf(adminToDelete));
        var userToDelete =  adminToDelete.getUser();
        if(userToDelete.getGuuid().equals("puZjCDm_xCg")){
            throw new AdminExceptions.AdminCannotBeDeleted("No se puede eliminar el administrador root");
        }
        Set<Role> updatedRoles = new HashSet<>(adminToDelete.getUser().getRoles());
        updatedRoles.clear();
        userToDelete.setIsDeleted(true);
        adminToDelete.setUser(userToDelete);
        adminRepository.save(adminToDelete);
    }

    /**
     * Exporta los admins del sistema en un fichero json dado una lista de admins.
     * @param file el archivo json
     * @param users la lista de admins
     */
    @Override
    public void exportJson(File file, List<Admin> users) {
        log.info("Exportando admins a json");
        adminStorage.exportJson(file, users);
    }

    /**
     * Importa un fichero con admins para guardarlos en la base de datos.
     * @param file fichero json que se importa
     */
    @Override
    public void importJson(File file) {
        log.info("Importando admins desde json");
        List<Admin> admins = adminStorage.importJson(file);
        adminRepository.saveAll(admins);
    }
}
