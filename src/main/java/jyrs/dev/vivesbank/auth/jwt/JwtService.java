package jyrs.dev.vivesbank.auth.jwt;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Interfaz que define los servicios relacionados con la gestión de tokens JWT.
 * Proporciona métodos para extraer información del token, generar tokens JWT y verificar su validez.
 */
public interface JwtService {

    /**
     * Extrae el nombre de usuario del token JWT.
     *
     * @param token el token JWT del cual se extraerá el nombre de usuario.
     * @return el nombre de usuario extraído del token.
     * @throws IllegalArgumentException si el token es inválido o no contiene el nombre de usuario.
     */
    String extractUserName(String token);

    /**
     * Genera un token JWT basado en los detalles del usuario proporcionado.
     *
     * @param userDetails los detalles del usuario que se utilizarán para generar el token.
     * @return el token JWT generado.
     */
    String generateToken(UserDetails userDetails);

    /**
     * Verifica si el token JWT es válido para el usuario especificado.
     *
     * @param token el token JWT que se debe verificar.
     * @param userDetails los detalles del usuario que se utilizarán para validar el token.
     * @return {@code true} si el token es válido, {@code false} si el token no es válido.
     */
    boolean isTokenValid(String token, UserDetails userDetails);
}
