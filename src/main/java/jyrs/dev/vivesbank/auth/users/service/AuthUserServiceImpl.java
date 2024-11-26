package jyrs.dev.vivesbank.auth.users.service;

import jyrs.dev.vivesbank.auth.users.repositories.AuthUserRepository;
import jyrs.dev.vivesbank.users.users.exceptions.UserExceptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service("userDetailsService")
@Slf4j
public class AuthUserServiceImpl implements AuthUserService {
    private final AuthUserRepository userRepository;
    @Autowired
    public AuthUserServiceImpl(AuthUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UserExceptions.UserNotFound {
        if(isGuuid(username)){
            log.info("loadByGuuid: " + username);
            return userRepository.findByGuuid(username)
                    .orElseThrow(() -> new UserExceptions.UserNotFound("no se ha encontrado usuario con guuid: " + username));
        }else {
            log.info("loadByUsername: " + username);
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new UserExceptions.UserNotFound("no se ha encontrado usuario con username: " + username));
        }

    }
    private boolean isGuuid(String guuid){
        return guuid.matches("^[A-Za-z0-9-_]{11}$");
    }
}
