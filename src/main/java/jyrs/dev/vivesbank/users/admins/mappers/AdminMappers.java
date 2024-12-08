package jyrs.dev.vivesbank.users.admins.mappers;

import jyrs.dev.vivesbank.users.admins.dto.AdminRequestDto;
import jyrs.dev.vivesbank.users.admins.dto.AdminResponseDto;
import jyrs.dev.vivesbank.users.models.Admin;
import org.springframework.stereotype.Component;

/**
 * Mapper para la request y response dtos de admin.
 */
@Component
public class AdminMappers {
    /**
     * Mapea un adminRequestDto a un admin
     * @param RequestDto
     * @return admin
     */
    public Admin fromAdminDto(AdminRequestDto RequestDto){
        return Admin.builder()
                .guuid(RequestDto.getGuuid()).build();
    }

    /**
     * Mapea un admin a un adminResponseDto
     * @param admin
     * @return adminResponseDto
     */
    public AdminResponseDto fromAdminToResponse(Admin admin){
        return AdminResponseDto.builder()
                .guuid(admin.getGuuid())
                .username(admin.getUser().getUsername())
                .fotoPerfil(admin.getUser().getFotoPerfil())
                .isDeleted(admin.getUser().getIsDeleted()).build();
    }
}
