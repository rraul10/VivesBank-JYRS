package jyrs.dev.vivesbank.users.admins.service;

import jyrs.dev.vivesbank.users.admins.dto.AdminRequestDto;
import jyrs.dev.vivesbank.users.admins.dto.AdminResponseDto;
import jyrs.dev.vivesbank.users.admins.dto.AdminUpdateRequest;
import jyrs.dev.vivesbank.users.admins.exceptions.AdminExceptions;
import jyrs.dev.vivesbank.users.admins.mappers.AdminMappers;
import jyrs.dev.vivesbank.users.admins.repository.AdminRepository;
import jyrs.dev.vivesbank.users.admins.services.AdminServiceImpl;
import jyrs.dev.vivesbank.users.admins.storage.AdminStorage;
import jyrs.dev.vivesbank.users.models.Admin;
import jyrs.dev.vivesbank.users.models.Role;
import jyrs.dev.vivesbank.users.models.User;
import jyrs.dev.vivesbank.users.users.exceptions.UserExceptions;
import jyrs.dev.vivesbank.users.users.repositories.UsersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceImplTests {
    private final User user = User.builder()
            .username("usuario@correo.com")
            .guuid("puZjCDm_xCg")
            .password("17j$e7cS")
            .fotoPerfil("profile.jpg")
            .roles(Set.of( Role.USER))
            .build();
    private final Admin admin = Admin.builder()
            .guuid("puZjCDm_xCg")
            .user(user).build();
    private final AdminResponseDto adminResponseDto = AdminResponseDto.builder()
            .guuid(admin.getGuuid())
            .username(user.getUsername())
            .fotoPerfil(user.getFotoPerfil())
            .isDeleted(false)
            .build();
    @Mock
    private AdminRepository adminRepository;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private AdminStorage adminStorage;
    @Mock
    private AdminMappers adminMapper;
    @Mock
    private UsersRepository userRepository;
    @InjectMocks
    private AdminServiceImpl adminService;

    @Test
    void getAllAdmins_NoArgsProvided(){
        List<Admin> admins = Arrays.asList(admin);
        List<AdminResponseDto> expectedResponseAdmins = Arrays.asList(adminResponseDto);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Admin> expectedPage = new PageImpl<>(admins);
        when(adminRepository.findAll(any(Specification.class),any(Pageable.class))).thenReturn(expectedPage);
        when(adminMapper.fromAdminToResponse(any(Admin.class))).thenReturn(adminResponseDto);
        Page<AdminResponseDto> actualResponseAdmins = adminService.getAllAdmins(Optional.empty(), Optional.empty(), pageable);
        assertAll(
                () -> assertNotNull(actualResponseAdmins),
                () -> assertEquals(expectedResponseAdmins, actualResponseAdmins.getContent())
        );
        verify(adminRepository, times(1)).findAll(any(Specification.class),any(Pageable.class));
        verify(adminMapper, times(1)).fromAdminToResponse(any(Admin.class));
    }
    @Test
    void getAllAdmins_UserNameProvided(){
        List<Admin> admins = Arrays.asList(admin);
        Optional<String> usernameProvided = Optional.of("usuario@correo.com");
        List<AdminResponseDto> expectedResponseAdmins = Arrays.asList(adminResponseDto);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Admin> expectedPage = new PageImpl<>(admins);
        when(adminRepository.findAll(any(Specification.class) ,any(Pageable.class))).thenReturn(expectedPage);
        when(adminMapper.fromAdminToResponse(any(Admin.class))).thenReturn(adminResponseDto);
        Page<AdminResponseDto> actualResponseAdmins = adminService.getAllAdmins(usernameProvided, Optional.empty(), pageable);
        assertAll(
                () -> assertNotNull(actualResponseAdmins),
                () -> assertEquals(expectedResponseAdmins, actualResponseAdmins.getContent())
        );
        verify(adminRepository, times(1)).findAll(any(Specification.class) ,any(Pageable.class));
        verify(adminMapper, times(1)).fromAdminToResponse(any(Admin.class));
    }
    @Test
    void getAllAdmins_IsDeletedProvided(){
        List<Admin> admins = Arrays.asList(admin);
        Optional<Boolean> isDeleted = Optional.of(false);
        List<AdminResponseDto> expectedResponseAdmins = Arrays.asList(adminResponseDto);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Admin> expectedPage = new PageImpl<>(admins);
        when(adminRepository.findAll(any(Specification.class),any(Pageable.class))).thenReturn(expectedPage);
        when(adminMapper.fromAdminToResponse(any(Admin.class))).thenReturn(adminResponseDto);
        Page<AdminResponseDto> actualResponseAdmins = adminService.getAllAdmins(Optional.empty(), isDeleted, pageable);
        assertAll(
                () -> assertNotNull(actualResponseAdmins),
                () -> assertEquals(expectedResponseAdmins, actualResponseAdmins.getContent())
        );
        verify(adminRepository, times(1)).findAll(any(Specification.class) ,any(Pageable.class));
        verify(adminMapper, times(1)).fromAdminToResponse(any(Admin.class));
    }

    @Test
    void getAllAdmins_AllArgsProvided(){
        List<Admin> admins = Arrays.asList(admin);
        Optional<String> usernameProvided = Optional.of("usuario@correo.com");
        Optional<Boolean> isDeleted = Optional.of(false);
        List<AdminResponseDto> expectedResponseAdmins = Arrays.asList(adminResponseDto);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Admin> expectedPage = new PageImpl<>(admins);
        when(adminRepository.findAll(any(Specification.class),any(Pageable.class))).thenReturn(expectedPage);
        when(adminMapper.fromAdminToResponse(any(Admin.class))).thenReturn(adminResponseDto);
        Page<AdminResponseDto> actualResponseAdmins = adminService.getAllAdmins(usernameProvided, isDeleted, pageable);
        assertAll(
                () -> assertNotNull(actualResponseAdmins),
                () -> assertEquals(expectedResponseAdmins, actualResponseAdmins.getContent())
        );
        verify(adminRepository, times(1)).findAll(any(Specification.class) ,any(Pageable.class));
        verify(adminMapper, times(1)).fromAdminToResponse(any(Admin.class));
    }

    @Test
    void getAdminById(){
        when(adminRepository.findByGuuid(anyString())).thenReturn(admin);
        when(adminMapper.fromAdminToResponse(any(Admin.class))).thenReturn(adminResponseDto);
        AdminResponseDto actualResponseAdmin = adminService.getAdminByGuuid(admin.getGuuid());
        assertEquals(adminResponseDto, actualResponseAdmin);
        verify(adminRepository, times(1)).findByGuuid(anyString());
        verify(adminMapper, times(1)).fromAdminToResponse(any(Admin.class));
    }
    @Test
    void getAdminByIdNotFound(){
        when(adminRepository.findByGuuid(anyString())).thenThrow(new AdminExceptions.AdminNotFound("No se ha encontrado admin con guuid: "+ admin.getGuuid()));
        var result = assertThrows(AdminExceptions.AdminNotFound.class, () -> adminService.getAdminByGuuid("adadada"));
        verify(adminRepository, times(1)).findByGuuid(anyString());
    }

    @Test
    void saveAdmin() throws AdminExceptions.AdminAlreadyExists {
        AdminRequestDto adminRequestDto = AdminRequestDto.builder()
                .guuid("puZjCDm_xCg").build();
        when(adminRepository.findByGuuid(anyString())).thenReturn(null);
        when(userRepository.findByGuuid(anyString())).thenReturn(user);
        when(adminRepository.save(any(Admin.class))).thenReturn(admin);
        when(adminMapper.fromAdminDto(adminRequestDto)).thenReturn(admin);
        when(adminMapper.fromAdminToResponse(admin)).thenReturn(adminResponseDto);
        var res = adminService.saveAdmin(adminRequestDto);
        assertAll(
                () -> assertEquals(adminResponseDto, res),
                () -> verify(adminRepository, times(1)).save(any(Admin.class)),
                () -> assertEquals(admin.getUser().getRoles().size(), 2)
        );
        verify(userRepository, times(1)).findByGuuid(anyString());
        verify(adminMapper, times(1)).fromAdminDto(adminRequestDto);
        verify(adminMapper, times(1)).fromAdminToResponse(admin);
        verify(adminRepository, times(1)).findByGuuid(anyString());
    }

    @Test
    void saveAdminAlreadyExists() {
        AdminRequestDto adminRequestDto = AdminRequestDto.builder()
                .guuid("puZjCDm_xCg").build();
        when(adminMapper.fromAdminDto(adminRequestDto)).thenReturn(admin);
        when(adminRepository.findByGuuid(anyString())).thenThrow(new AdminExceptions.AdminAlreadyExists("Admin already exists with guid: "+ adminRequestDto.getGuuid()));
        var result = assertThrows(AdminExceptions.AdminAlreadyExists.class, () -> adminService.saveAdmin(adminRequestDto));
        verify(adminRepository, times(1)).findByGuuid(anyString());
    }
    @Test
    void saveAdminNoExistingUser(){
        AdminRequestDto adminRequestDto = AdminRequestDto.builder()
                .guuid("puZjCDm_xCg").build();
        when(adminMapper.fromAdminDto(adminRequestDto)).thenReturn(admin);
        when(adminRepository.findByGuuid(anyString())).thenReturn(null);
        when(userRepository.findByGuuid(anyString())).thenThrow(new UserExceptions.UserNotFound("No se ha encontrado usuario con guuid: "+ adminRequestDto.getGuuid()));
        var result = assertThrows(UserExceptions.UserNotFound.class, () -> adminService.saveAdmin(adminRequestDto));
        verify(adminRepository, times(1)).findByGuuid(anyString());
        verify(userRepository, times(1)).findByGuuid(anyString());
    }

    @Test
    void updateAdmin(){
        AdminUpdateRequest adminUpdateRequest = AdminUpdateRequest.builder()
                .username("updated@gmail.com")
                .guuid(user.getGuuid())
                .fotoPerfil("fotoUpdated.png").build();
        when(adminRepository.findByGuuid(anyString())).thenReturn(admin);
        when(adminRepository.save(admin)).thenReturn(admin);
        when(adminMapper.fromAdminToResponse(admin)).thenReturn(adminResponseDto);
        var res = adminService.updateAdmin(user.getGuuid() ,adminUpdateRequest);
        assertAll(
                () -> assertEquals(adminResponseDto, res),
                () -> verify(adminRepository, times(1)).save(any(Admin.class)),
                () -> assertEquals(admin.getUser().getUsername(), adminUpdateRequest.getUsername())
        );
    }
    @Test
    void updateAdminNotFound(){
        AdminUpdateRequest adminUpdateRequest = AdminUpdateRequest.builder()
                .username("updated@gmail.com")
                .guuid("asdadada")
                .fotoPerfil("fotoUpdated.png").build();
        when(adminRepository.findByGuuid(adminUpdateRequest.getGuuid())).thenThrow(AdminExceptions.AdminNotFound.class);
        var res = assertThrows(AdminExceptions.AdminNotFound.class ,() -> adminService.updateAdmin(adminUpdateRequest.getGuuid(), adminUpdateRequest ));
        verify(adminRepository, times(1)).findByGuuid(adminUpdateRequest.getGuuid());
    }

    @Test
    void deleteAdmin(){
        User userToDelete = User.builder()
                .username("usuario@correo.com")
                .guuid("puZjCDm_xCc")
                .password("17j$e7cS")
                .fotoPerfil("profile.jpg")
                .roles(Set.of( Role.USER))
                .build();
         Admin adminToDelte = Admin.builder()
                .guuid("puZjCDm_xCc")
                .user(userToDelete).build();
        when(adminRepository.findByGuuid(anyString())).thenReturn(adminToDelte);
        adminService.deleteAdmin(adminToDelte.getGuuid());
        verify(adminRepository, times(1)).findByGuuid(anyString());
    }

    @Test
    void deleteAdmin_CannotSuperAdmin(){
        when(adminRepository.findByGuuid(anyString())).thenReturn(admin);
        var result = assertThrows(AdminExceptions.AdminCannotBeDeleted.class, () -> adminService.deleteAdmin(admin.getGuuid()));
        verify(adminRepository, times(1)).findByGuuid(anyString());
    }
    @Test
    void deleteAdminNotFound(){
        when(adminRepository.findByGuuid(anyString())).thenThrow(new AdminExceptions.AdminNotFound("No se ha encontrado admin con guuid: "+ admin.getGuuid()));
        var result = assertThrows(AdminExceptions.AdminNotFound.class, () -> adminService.deleteAdmin(admin.getGuuid()));
        verify(adminRepository, times(1)).findByGuuid(anyString());
    }

    @Test
    void importJson() throws Exception {
        File file = mock(File.class);
        List<Admin> admins = List.of(admin);

        when(adminStorage.importJson(file)).thenReturn(admins);

        adminService.importJson(file);

        verify(adminStorage, times(1)).importJson(file);

        verify(adminRepository, times(1)).saveAll(admins);
    }

    @Test
    void exportJson() throws Exception {
        File file = mock(File.class);
        List<Admin> admins = List.of(admin);

        doNothing().when(adminStorage).exportJson(file,admins);
        adminService.exportJson(file, admins);

        verify(adminStorage, times(1)).exportJson(file, admins);
    }
}
