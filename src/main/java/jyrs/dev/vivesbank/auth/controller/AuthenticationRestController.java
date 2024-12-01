package jyrs.dev.vivesbank.auth.controller;

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

@RestController
@Slf4j
@RequestMapping("${api.path:/api}/${api.version:/v1}/auth")
public class AuthenticationRestController {
    private final AuthService authService;
    @Autowired
    public AuthenticationRestController(AuthService authService) {
        this.authService = authService;
    }
    @PostMapping("/signup")
    public ResponseEntity<JwtAuthResponse> signUp(@Valid @RequestBody UserSignUpRequest request) throws UserAuthNameOrEmailExisten, AuthSignUpInvalid {
        log.info("Registrando usuario: {}", request);
        return ResponseEntity.ok(authService.singUp(request));
    }

    @PostMapping("/signin")
    public ResponseEntity<JwtAuthResponse> signIn(@Valid @RequestBody UserSignInRequest request) throws AuthSignUpInvalid {
        log.info("Iniciando sesión de usuario: {}", request);
        return ResponseEntity.ok(authService.signIn(request));
    }
}
