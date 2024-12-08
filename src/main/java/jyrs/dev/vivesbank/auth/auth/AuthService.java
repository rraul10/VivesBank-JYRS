package jyrs.dev.vivesbank.auth.auth;

import jyrs.dev.vivesbank.auth.dto.JwtAuthResponse;
import jyrs.dev.vivesbank.auth.dto.UserSignInRequest;
import jyrs.dev.vivesbank.auth.dto.UserSignUpRequest;
import jyrs.dev.vivesbank.auth.exception.AuthSignUpInvalid;
import jyrs.dev.vivesbank.auth.exception.UserAuthNameOrEmailExisten;

/**
 * Servicio de autenticación que maneja el registro y el inicio de sesión de los usuarios.
 * Este servicio permite registrar nuevos usuarios y autenticar a usuarios existentes.
 */
public interface AuthService {

    /**
     * Registra un nuevo usuario en el sistema. Valida los datos de entrada, crea el usuario y genera un token JWT.
     *
     * @param request Objeto que contiene los datos necesarios para registrar un nuevo usuario.
     * @return JwtAuthResponse Objeto que contiene el token JWT generado para el usuario registrado.
     * @throws UserAuthNameOrEmailExisten Si el nombre de usuario o correo electrónico ya están registrados en el sistema.
     * @throws AuthSignUpInvalid Si los datos del registro no son válidos, como un nombre de usuario o correo electrónico mal formateado.
     */
    JwtAuthResponse singUp(UserSignUpRequest request) throws UserAuthNameOrEmailExisten, AuthSignUpInvalid;

    /**
     * Inicia sesión de un usuario. Verifica las credenciales del usuario y genera un token JWT.
     *
     * @param request Objeto que contiene el nombre de usuario y la contraseña para el inicio de sesión.
     * @return JwtAuthResponse Objeto que contiene el token JWT generado para el usuario autenticado.
     * @throws AuthSignUpInvalid Si las credenciales del usuario no son válidas o están incorrectas.
     */
    JwtAuthResponse signIn(UserSignInRequest request) throws AuthSignUpInvalid;
}
