package jyrs.dev.vivesbank.users.admins.services;

import jyrs.dev.vivesbank.users.admins.dto.AdminRequestDto;
import jyrs.dev.vivesbank.users.admins.dto.AdminResponseDto;
import jyrs.dev.vivesbank.users.admins.dto.AdminUpdateRequest;
import jyrs.dev.vivesbank.users.admins.exceptions.AdminExceptions;
import jyrs.dev.vivesbank.users.models.Admin;
import jyrs.dev.vivesbank.users.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.util.List;
import java.util.Optional;

public interface AdminService {
    Page<AdminResponseDto> getAllAdmins(Optional<String> username, Optional<Boolean> isDeleted, Pageable pageable);
    AdminResponseDto getAdminByGuuid(String id);
    AdminResponseDto saveAdmin(AdminRequestDto requestDto) throws AdminExceptions.AdminAlreadyExists;
    AdminResponseDto updateAdmin(String id, AdminUpdateRequest user);
    void deleteAdmin(String id) throws AdminExceptions.AdminCannotBeDeleted;
    void exportJson(File file, List<Admin> admins);
    void importJson(File file);
}
