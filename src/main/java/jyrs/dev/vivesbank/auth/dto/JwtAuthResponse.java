package jyrs.dev.vivesbank.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase que representa la respuesta de autenticación JWT.
 * Esta clase se utiliza para devolver el token JWT generado después de una autenticación exitosa.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthResponse {

    /**
     * El token JWT generado para la autenticación.
     * Este token es utilizado para autenticar al usuario en solicitudes posteriores.
     */
    private String token;
}

