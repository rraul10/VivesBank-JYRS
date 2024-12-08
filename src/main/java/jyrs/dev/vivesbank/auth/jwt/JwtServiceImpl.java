package jyrs.dev.vivesbank.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jyrs.dev.vivesbank.users.models.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Implementación del servicio JwtService para la gestión de tokens JWT.
 * Esta clase proporciona métodos para extraer el nombre de usuario de un token, generar un token
 * para un usuario y verificar si un token es válido.
 */
@Service
@Slf4j
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret}")
    private String jwtSignInKey;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    /**
     * Extrae el nombre de usuario del token JWT.
     *
     * @param token el token JWT del cual se extraerá el nombre de usuario.
     * @return el nombre de usuario extraído del token.
     */
    @Override
    public String extractUserName(String token) {
        log.info("Extracting user name from token " + token);
        return extractClaim(token, DecodedJWT::getSubject);
    }

    /**
     * Genera un token JWT para un usuario dado.
     *
     * @param userDetails los detalles del usuario que se utilizarán para generar el token.
     * @return el token JWT generado.
     */
    @Override
    public String generateToken(UserDetails userDetails) {
        log.info("Generating token for user: " + userDetails.getUsername());
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Verifica si un token JWT es válido para un usuario dado.
     *
     * @param token el token JWT que se debe verificar.
     * @param userDetails los detalles del usuario que se utilizarán para validar el token.
     * @return {@code true} si el token es válido, {@code false} si el token no es válido.
     */
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        log.info("Exctracting claim from token: " + token);
        final String username = extractUserName(token);
        log.info("extrayendo username para comprobar validez: " + username);
        log.info("username userDetails: " + userDetails.getUsername());
        return (username.equals(((User) userDetails).getGuuid())) && !isTokenExpired(token);
    }

    /**
     * Verifica si un token JWT ha expirado.
     *
     * @param token el token JWT que se debe verificar.
     * @return {@code true} si el token ha expirado, {@code false} si el token no ha expirado.
     */
    private boolean isTokenExpired(String token) {
        Date expirationDate = extractExpiration(token);
        return expirationDate.before(new Date());
    }

    /**
     * Extrae la fecha de expiración del token JWT.
     *
     * @param token el token JWT del cual se extraerá la fecha de expiración.
     * @return la fecha de expiración del token.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, DecodedJWT::getExpiresAt);
    }

    /**
     * Extrae una reclamación específica del token JWT.
     *
     * @param token el token JWT del cual se extraerá la reclamación.
     * @param claimsResolvers una función que resuelve la reclamación.
     * @param <T> el tipo de la reclamación.
     * @return el valor de la reclamación extraída.
     */
    private <T> T extractClaim(String token, Function<DecodedJWT, T> claimsResolvers) {
        log.info("Extracting claim from token " + token);
        final DecodedJWT decodedJWT = JWT.decode(token);
        return claimsResolvers.apply(decodedJWT);
    }

    /**
     * Genera un token JWT para un usuario con los detalles proporcionados.
     *
     * @param extraClaims reclamaciones adicionales que se incluirán en el token.
     * @param userDetails los detalles del usuario que se utilizarán para generar el token.
     * @return el token JWT generado.
     */
    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails){
        Algorithm algorithm = Algorithm.HMAC512(getSignInKey());
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + (1000 *  jwtExpiration));
        String guuid = ((User) userDetails).getGuuid();
        return JWT.create()
                .withHeader(createHeader())
                .withSubject(guuid)
                .withIssuedAt(now)
                .withExpiresAt(expirationDate)
                .withClaim("extraClaims", extraClaims)
                .sign(algorithm);
    }

    /**
     * Crea el encabezado estándar para el token JWT.
     *
     * @return un mapa con el encabezado del token JWT.
     */
    private Map<String, Object> createHeader() {
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        return header;
    }

    /**
     * Obtiene la clave secreta utilizada para firmar los tokens JWT, codificada en Base64.
     *
     * @return la clave secreta en formato de bytes codificados en Base64.
     */
    private byte[] getSignInKey() {
        return Base64.getEncoder().encode(jwtSignInKey.getBytes());
    }
}
