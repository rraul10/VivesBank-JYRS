package jyrs.dev.vivesbank.users.users.services;

import jyrs.dev.vivesbank.users.models.User;
import jyrs.dev.vivesbank.users.users.dto.UserRequestDto;
import jyrs.dev.vivesbank.users.users.dto.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.util.List;
import java.util.Optional;

public interface UsersService {
    Page<UserResponseDto> getAllUsers(Optional<String> username, Optional<Boolean> isDeleted, Pageable pageable);
    UserResponseDto getUserById(String id);
    UserResponseDto getUserByName(String name);
    UserResponseDto saveUser(UserRequestDto user);
    UserResponseDto updateUser(String id, UserRequestDto user);
    void deleteUser(String id);
    void exportJson(File file, List<User> users);
    void importJson(File file);
}
