package jyrs.dev.vivesbank.users.users.services;

import jyrs.dev.vivesbank.users.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UsersService {
    Page<User> getAllUsers(Optional<String> username, Optional<Boolean> isDeleted, Pageable pageable);
    User getUserById(Long id);
    User getUserByName(String name);
    User saveUser(User user);
    User updateUser(Long id, User user);
    void deleteUser(Long id);
}
