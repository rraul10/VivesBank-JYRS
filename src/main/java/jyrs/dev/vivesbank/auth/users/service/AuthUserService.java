package jyrs.dev.vivesbank.auth.users.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthUserService extends UserDetailsService {
    @Override
    UserDetails loadUserByUsername(String username);
}
