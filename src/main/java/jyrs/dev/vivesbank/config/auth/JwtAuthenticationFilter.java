package jyrs.dev.vivesbank.config.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jyrs.dev.vivesbank.auth.jwt.JwtService;
import jyrs.dev.vivesbank.auth.users.service.AuthUserService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
/**
 * Filtro de autenticación basado en JWT que intercepta las solicitudes HTTP para validar el token de autenticación.
 * Este filtro extrae el token de la cabecera de autorización, verifica su validez y, si el token es válido,
 * autentica al usuario configurando el contexto de seguridad de Spring.
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final AuthUserService authUserService;
    /**
     * Constructor que inicializa el filtro con los servicios necesarios para la validación del JWT y carga del usuario.
     *
     * @param jwtService Servicio que maneja la validación y extracción de información del token JWT.
     * @param authUserService Servicio que carga los detalles del usuario a partir de su nombre de usuario.
     */
    @Autowired
    public JwtAuthenticationFilter(JwtService jwtService, AuthUserService authUserService) {
        this.jwtService = jwtService;
        this.authUserService = authUserService;
    }
    /**
     * Método que se ejecuta para filtrar las solicitudes HTTP. Extrae el token JWT de la cabecera de autorización
     * y valida su autenticidad. Si el token es válido, se autentica al usuario y se establece el contexto de seguridad.
     *
     * @param request La solicitud HTTP que contiene la cabecera de autorización con el token JWT.
     * @param response La respuesta HTTP que se envía al cliente.
     * @param filterChain La cadena de filtros por la que pasa la solicitud después de la validación.
     * @throws ServletException Si ocurre un error en el procesamiento del filtro.
     * @throws IOException Si ocurre un error en el procesamiento de la respuesta.
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        log.info("Iniciando el filtro de autenticación");
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        UserDetails userDetails = null;
        String userName = null;

        if (!StringUtils.hasText(authHeader) || !StringUtils.startsWithIgnoreCase(authHeader, "Bearer")) {
            log.info("No hay cabecera de autorización, continuando con la petición");
            filterChain.doFilter(request, response);
            return;
        }

        log.info("Se ha encontrado cabecera de autenticación, procesando...");
        jwt = authHeader.substring(7);
        try {
            userName = jwtService.extractUserName(jwt);
        } catch (Exception e) {
            log.error("Token no válido");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token no autorizado o inválido");
            return;
        }

        if (StringUtils.hasText(userName) && SecurityContextHolder.getContext().getAuthentication() == null) {
            log.info("Comprobando usuario y token");
            try {
                userDetails = authUserService.loadUserByUsername(userName);
            } catch (Exception e) {
                log.info("Usuario no encontrado: {}", userName);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Usuario no autorizado");
                return;
            }
            log.info("user encontrado : " + userDetails);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                log.info("JWT válido");
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);
            }
        }

        filterChain.doFilter(request, response);
    }

}

