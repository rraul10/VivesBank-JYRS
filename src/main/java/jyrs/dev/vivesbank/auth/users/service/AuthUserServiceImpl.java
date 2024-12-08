package jyrs.dev.vivesbank.auth.users.service;

import jyrs.dev.vivesbank.auth.users.repositories.AuthUserRepository;
import jyrs.dev.vivesbank.users.users.exceptions.UserExceptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Implementación del servicio de autenticación para cargar usuarios desde el repositorio.
 * Esta clase se encarga de proporcionar los detalles del usuario necesarios para la autenticación.
 * Soporta la carga de usuarios por su nombre de usuario o por un identificador único (guuid).
 */
@Service("userDetailsService")
@Slf4j
public class AuthUserServiceImpl implements AuthUserService {

    /**
     * Repositorio para acceder a los datos de autenticación de los usuarios.
     */
    private final AuthUserRepository userRepository;

    /**
     * Constructor que inyecta el repositorio de usuarios.
     *
     * @param userRepository el repositorio de usuarios utilizado para acceder a los datos de autenticación.
     */
    @Autowired
    public AuthUserServiceImpl(AuthUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Carga los detalles de un usuario por su nombre de usuario o identificador único (guuid).
     *
     * @param username el nombre de usuario o identificador único del usuario.
     * @return un objeto {@link UserDetails} que contiene los detalles del usuario.
     * @throws UserExceptions.UserNotFound si no se encuentra un usuario con el nombre de usuario o guuid proporcionado.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UserExceptions.UserNotFound {
        if (isGuuid(username)) {
            log.info("loadByGuuid: " + username);
            return userRepository.findByGuuid(username)
                    .orElseThrow(() -> new UserExceptions.UserNotFound("No se ha encontrado usuario con guuid: " + username));
        } else {
            log.info("loadByUsername: " + username);
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new UserExceptions.UserNotFound("No se ha encontrado usuario con username: " + username));
        }
    }

    /**
     * Verifica si una cadena de texto corresponde a un identificador único (guuid).
     *
     * @param guuid el valor a verificar.
     * @return {@code true} si el valor corresponde a un guuid, {@code false} en caso contrario.
     */
    private boolean isGuuid(String guuid) {
        return guuid.matches("^[A-Za-z0-9-_]{11}$");
    }
}
