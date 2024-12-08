package jyrs.dev.vivesbank.auth.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jyrs.dev.vivesbank.auth.auth.AuthService;
import jyrs.dev.vivesbank.auth.dto.JwtAuthResponse;
import jyrs.dev.vivesbank.auth.dto.UserSignInRequest;
import jyrs.dev.vivesbank.auth.dto.UserSignUpRequest;
import jyrs.dev.vivesbank.auth.exception.AuthSignUpInvalid;
import jyrs.dev.vivesbank.auth.exception.UserAuthNameOrEmailExisten;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para manejar la autenticación de usuarios, incluyendo el registro y el inicio de sesión.
 * Proporciona endpoints para registrar un nuevo usuario y autenticar a un usuario existente.
 */
@RestController
@Slf4j
@RequestMapping("${api.path:/api}${api.version:/v1}/auth")
public class AuthenticationRestController {

    private final AuthService authService;

    @Autowired
    public AuthenticationRestController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint para registrar un nuevo usuario.
     * Este endpoint recibe la solicitud de registro de un nuevo usuario, incluyendo su nombre de usuario, contraseña y foto de perfil.
     *
     * @param request El objeto que contiene los datos del nuevo usuario.
     * @return ResponseEntity con el token JWT generado para el usuario registrado.
     * @throws UserAuthNameOrEmailExisten Si el nombre de usuario o el correo electrónico ya están registrados.
     * @throws AuthSignUpInvalid Si hay un error en los datos proporcionados para el registro.
     */
    @Operation(
            summary = "Registra un nuevo usuario",
            description = "Este endpoint permite a un usuario registrarse en el sistema proporcionando un nombre de usuario, contraseña y foto de perfil."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro exitoso, se devuelve el token JWT.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtAuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de registro inválidos.",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "El nombre de usuario o correo electrónico ya existe.",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.", content = @Content)
    })
    @PostMapping("/signup")
    public ResponseEntity<JwtAuthResponse> signUp(
            @Valid @RequestBody UserSignUpRequest request) throws UserAuthNameOrEmailExisten, AuthSignUpInvalid {
        log.info("Registrando usuario: {}", request);
        return ResponseEntity.ok(authService.singUp(request));
    }

    /**
     * Endpoint para iniciar sesión de un usuario.
     * Este endpoint permite a un usuario autenticar su nombre de usuario y contraseña para obtener un token JWT.
     *
     * @param request El objeto que contiene el nombre de usuario y la contraseña.
     * @return ResponseEntity con el token JWT generado para el usuario autenticado.
     * @throws AuthSignUpInvalid Si los datos proporcionados son incorrectos o no válidos para la autenticación.
     */
    @Operation(
            summary = "Inicia sesión de un usuario",
            description = "Este endpoint permite a un usuario iniciar sesión proporcionando su nombre de usuario y contraseña para obtener un token JWT."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso, se devuelve el token JWT.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtAuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de inicio de sesión inválidos.",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas.",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.", content = @Content)
    })
    @PostMapping("/signin")
    public ResponseEntity<JwtAuthResponse> signIn(
            @Valid @RequestBody UserSignInRequest request) throws AuthSignUpInvalid {
        log.info("Iniciando sesión de usuario: {}", request);
        return ResponseEntity.ok(authService.signIn(request));
    }
}

