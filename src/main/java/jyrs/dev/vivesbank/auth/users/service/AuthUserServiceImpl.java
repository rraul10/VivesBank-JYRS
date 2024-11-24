package jyrs.dev.vivesbank.auth.users.service;

import jyrs.dev.vivesbank.auth.users.repositories.AuthUserRepository;
import jyrs.dev.vivesbank.users.users.exceptions.UserExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service("userDetailsService")
public class AuthUserServiceImpl implements AuthUserService {
    private final AuthUserRepository userRepository;
    @Autowired
    public AuthUserServiceImpl(AuthUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String guuid) throws UserExceptions.UserNotFound {
        return userRepository.findByGuuid(guuid)
                .orElseThrow(() -> new UserExceptions.UserNotFound("no se ha encontrado usuario con uniqueId: " + guuid));
    }
}
