package jyrs.dev.vivesbank.auth.auth;

import jyrs.dev.vivesbank.auth.dto.JwtAuthResponse;
import jyrs.dev.vivesbank.auth.dto.UserSignInRequest;
import jyrs.dev.vivesbank.auth.dto.UserSignUpRequest;
import jyrs.dev.vivesbank.auth.exception.AuthSignUpInvalid;
import jyrs.dev.vivesbank.auth.exception.UserAuthNameOrEmailExisten;
import jyrs.dev.vivesbank.auth.exception.UserDiferentePasswords;
import jyrs.dev.vivesbank.auth.exception.UserPasswordBadRequest;
import jyrs.dev.vivesbank.auth.jwt.JwtService;
import jyrs.dev.vivesbank.auth.users.repositories.AuthUserRepository;
import jyrs.dev.vivesbank.auth.validator.UserValidator;
import jyrs.dev.vivesbank.users.models.Role;
import jyrs.dev.vivesbank.users.models.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.stream.Stream;



@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserValidator userValidator;

    @Autowired
    public AuthServiceImpl(AuthUserRepository authUserRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager, UserValidator userValidator) {
        this.authUserRepository = authUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userValidator = userValidator;
    }

    @Override
    public JwtAuthResponse singUp(UserSignUpRequest request) throws UserAuthNameOrEmailExisten, AuthSignUpInvalid {
        log.info("Creando usuario: {}" , request);
        if(!userValidator.validateUserName(request.getUsername())){
            throw new AuthSignUpInvalid("El nombre de usuario debe de ser un correo electrónico válido: " + request.getUsername());
        }
        if(!userValidator.validatePassword(request.getPassword())){
            throw new UserPasswordBadRequest("Las contraseñas no puede tener menos de 8 caracteres....");
        }
        if(request.getPassword().contentEquals(request.getCheckPassword())){
            User user = User.builder()
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .fotoPerfil(request.getFotoPerfil())
                    .roles(Stream.of(Role.USER).collect(Collectors.toSet()))
                    .build();
            try {
                var userStored = authUserRepository.save(user);
                return JwtAuthResponse.builder().token(jwtService.generateToken(userStored)).build();
            }catch (DataIntegrityViolationException ex){
                throw new UserAuthNameOrEmailExisten("El usuario con username " + request.getUsername() + " ya existe.");
            }
        }else {
            throw new UserDiferentePasswords("Las contraseñas no coinciden");
        }
    }



    @Override
    public JwtAuthResponse signIn(UserSignInRequest request) throws AuthSignUpInvalid {
        log.info("Autenticando usuario: {}", request);
        var user = authUserRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AuthSignUpInvalid("Usuario o contraseña incorrectos"));
        log.info("Autenticando usuario: {}", request);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        log.info("Autenticando usuario: {}", request);

        var jwt = jwtService.generateToken(user);
        return JwtAuthResponse.builder().token(jwt).build();
    }
}
