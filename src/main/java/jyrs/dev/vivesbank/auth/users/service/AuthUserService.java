package jyrs.dev.vivesbank.auth.users.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Servicio de autenticación de usuarios.
 * Extiende la interfaz {@link UserDetailsService} para proporcionar
 * una implementación personalizada del método de carga de usuarios.
 */
public interface AuthUserService extends UserDetailsService {

    /**
     * Carga los detalles de un usuario a partir de su nombre de usuario o identificador único.
     *
     * @param username el nombre de usuario o identificador único (guuid) del usuario.
     * @return un objeto {@link UserDetails} que representa los detalles del usuario autenticado.
     */
    @Override
    UserDetails loadUserByUsername(String username);
}
