package jyrs.dev.vivesbank.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class JwtServiceImpl implements JwtService {
    @Value("${jwt.secret}")
    private String jwtSignInKey;
    @Value("${jwt.expiration}")
    private Long jwtExpiration;
    @Override
    public String extractUserName(String token) {
        log.info("Extracting user name from token " + token);
        return extractClaim(token, DecodedJWT::getSubject);
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        log.info("Generating token for user: " + userDetails.getUsername());
        return generateToken(new HashMap<>(), userDetails);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        log.info("Exctracting claim from token: " + token);
        final String username = extractUserName(token);
        return (username.equals(userDetails.getUsername())) && !isTokenSpired(token);
    }

    private boolean isTokenSpired(String token) {
        Date expirationDate = extractExpiration(token);
        return expirationDate.before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, DecodedJWT::getExpiresAt);
    }

    private <T> T extractClaim(String token, Function<DecodedJWT, T> claimsResolvers) {
        log.info("Extracting claim from token " + token);
        final DecodedJWT decodedJWT = JWT.decode(token);
        return claimsResolvers.apply(decodedJWT);
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails){
        Algorithm algorithm = Algorithm.HMAC512(getSignInKey());
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + (1000 *  jwtExpiration));
        return JWT.create()
                .withHeader(createHeader())
                .withSubject(userDetails.getUsername())
                .withIssuedAt(now)
                .withExpiresAt(expirationDate)
                .withClaim("extraClaims", extraClaims)
                .sign(algorithm);
    }

    private Map<String, Object> createHeader() {
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        return header;
    }

    private byte[] getSignInKey() {
        return Base64.getEncoder().encode(jwtSignInKey.getBytes());
    }
}
