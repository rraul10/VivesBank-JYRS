package jyrs.dev.vivesbank.users.admins.services;

import jyrs.dev.vivesbank.users.admins.dto.AdminRequestDto;
import jyrs.dev.vivesbank.users.admins.dto.AdminUpdateRequest;
import jyrs.dev.vivesbank.users.admins.exceptions.AdminExceptions;
import jyrs.dev.vivesbank.users.admins.repository.AdminRepository;
import jyrs.dev.vivesbank.users.admins.dto.AdminResponseDto;
import jyrs.dev.vivesbank.users.admins.mappers.AdminMappers;
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
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AdminServiceImpl implements AdminService {
    private final AdminRepository adminRepository;
    private final AdminMappers adminMappers;
    private final UsersRepository usersRepository;
    @Autowired

    public AdminServiceImpl(AdminRepository adminRepository, AdminMappers adminMappers, UsersRepository usersRepository) {
        this.adminRepository = adminRepository;
        this.adminMappers = adminMappers;
        this.usersRepository = usersRepository;
    }

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


    @Override
    public AdminResponseDto getAdminByGuuid(String id) {
        log.info("Buscando admin con guuid: " + id);
        var res = adminMappers.fromAdminToResponse(adminRepository.findByGuuid(id));
        if (res == null){
            throw new AdminExceptions.AdminNotFound("No se ha encontrado admin con guuid: " + id);
        }
        return null;
    }

    @Override
    public AdminResponseDto saveAdmin(AdminRequestDto request) throws AdminExceptions.AdminAlreadyExists {
        log.info("Añadiendo nuevo admin al sistema" + request);
        var admin = adminMappers.fromAdminDto(request);
        if(adminRepository.findByGuuid(admin.getGuuid()) != null){
            throw new AdminExceptions.AdminAlreadyExists("Ya existe un admin con el mismo guuid");
        }
        var user = usersRepository.findByGuuid(request.getGuuid());
        if(user == null){
            throw new UserExceptions.UserNotFound("No se ha encontrado usuario con el guuid: " + request.getGuuid());
        }
        user.getRoles().add(Role.ADMIN);
        admin.setUser(user);
        admin.setGuuid(user.getGuuid());
        var adminGuardado = adminRepository.save(admin);
        return adminMappers.fromAdminToResponse(adminGuardado);
    }

    @Override
    public AdminResponseDto updateAdmin(String id, AdminUpdateRequest user) {
        log.info("Buscando administrador en el sistema: " + user);
        var adminToUpdate = adminRepository.findByGuuid(id);
        var userToUpdate = adminToUpdate.getUser();
        userToUpdate.setUsername(user.getUsername());
        userToUpdate.setFotoPerfil(user.getFotoPerfil());
        adminToUpdate.setUser(userToUpdate);
        adminRepository.save(adminToUpdate);
        return adminMappers.fromAdminToResponse(adminToUpdate);
    }

    @Override
    public void deleteAdmin(String id) throws AdminExceptions.AdminCannotBeDeleted {
        log.info("Eliminando admin con guuid: " + id);
        var adminToDelete = adminRepository.findByGuuid(id);
        var userToDelete =  adminToDelete.getUser();
        if(userToDelete.getGuuid().equals("puZjCDm_xCg")){
            throw new AdminExceptions.AdminCannotBeDeleted("No se puede eliminar el administrador root");
        }
        userToDelete.getRoles().remove(Role.ADMIN);
        userToDelete.setIsDeleted(true);
    }

    @Override
    public void exportJson(File file, List<User> users) {

    }

    @Override
    public void importJson(File file) {

    }
}
