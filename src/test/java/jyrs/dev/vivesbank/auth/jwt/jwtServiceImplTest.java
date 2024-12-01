package jyrs.dev.vivesbank.auth.jwt;

import jyrs.dev.vivesbank.auth.jwt.JwtServiceImpl;
import jyrs.dev.vivesbank.users.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class jwtServiceImplTest {
    private JwtServiceImpl jwtService;
    private User userMock;
    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl();

        // Configura valores privados usando ReflectionTestUtils
        ReflectionTestUtils.setField(jwtService, "jwtSignInKey", "testSecretKey");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600L); // 1 hora

        // Configura el usuario mock
        userMock = mock(User.class);
        when(userMock.getGuuid()).thenReturn("puZjCDm_xCg");
        when(userMock.getUsername()).thenReturn("testUser");
    }
    @Test
    void testGenerateToken() {
        String token = jwtService.generateToken(userMock);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }
    @Test
    void testExtractUserName() {
        String token = jwtService.generateToken(userMock);

        // Extraer el subject (UUID) del token
        String extractedUUID = jwtService.extractUserName(token);
        assertEquals("puZjCDm_xCg", extractedUUID);
    }
    @Test
    void testTokenExpiration() {
        // Genera un token y extrae su fecha de expiración
        String token = jwtService.generateToken(userMock);
        Date expiration = ReflectionTestUtils.invokeMethod(jwtService, "extractExpiration", token);
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void testIsTokenExpired() {
        // Configura un token expirado
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1L); // Expira inmediatamente

        String expiredToken = jwtService.generateToken(userMock);
        assertFalse(jwtService.isTokenValid(expiredToken, userMock)); // Debe ser inválido
    }

}
