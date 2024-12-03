package jyrs.dev.vivesbank.config.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final UserDetailsService userService;
    private final JwtAuthenticationFilter authenticationFilter;
    @Value("${api.version}")
    private String apiVersion;
    @Value("${api.path}")
    private String apipath;


    public SecurityConfig(UserDetailsService userService, JwtAuthenticationFilter authenticationFilter) {
        this.userService = userService;
        this.authenticationFilter = authenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(manager ->manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request ->request.requestMatchers("/error/**").permitAll())
                .authorizeHttpRequests(request -> request.requestMatchers(apipath + apiVersion + "/auth/**").permitAll())
                .authorizeHttpRequests(request ->request.requestMatchers(HttpMethod.GET,apipath + apiVersion + "/users" ).hasRole("ADMIN"))
                .authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.GET,apipath + apiVersion + "/users/me/profile").hasAnyRole("USER", "ADMIN"))
                .authorizeHttpRequests(request ->request.requestMatchers(HttpMethod.GET, apipath + apiVersion + "/users/{id}").hasRole("ADMIN"))
                .authorizeHttpRequests(request ->request.requestMatchers(HttpMethod.GET, apipath + apiVersion + "/users/name/{name}").hasRole("ADMIN"))
                .authorizeHttpRequests(request ->request.requestMatchers(HttpMethod.POST, apipath+ apiVersion + "/users").permitAll())
                .authorizeHttpRequests(request ->request.requestMatchers(HttpMethod.PUT, apipath + apiVersion + "/users/{id}").hasRole("ADMIN"))
                .authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.PUT,apipath + apiVersion + "/users/me/profile").hasAnyRole("USER", "ADMIN"))
                .authorizeHttpRequests(request ->request.requestMatchers(HttpMethod.DELETE, apipath + apiVersion + "/users/{id}").hasRole("ADMIN"))
                .authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.DELETE,apipath + apiVersion + "/auth/**").hasAnyRole("USER", "ADMIN"))
                 // clients
                .authorizeHttpRequests(request ->request.requestMatchers(HttpMethod.GET,"/storage" ).hasRole("ADMIN"))
                .authorizeHttpRequests(request ->request.requestMatchers(HttpMethod.GET,apipath + apiVersion + "/clients" ).hasRole("ADMIN"))
                .authorizeHttpRequests(request ->request.requestMatchers(HttpMethod.GET, apipath + apiVersion + "/clients/{id}").hasRole("ADMIN"))
                .authorizeHttpRequests(request ->request.requestMatchers(HttpMethod.GET, apipath + apiVersion + "/clients/dni/{dni}").hasRole("ADMIN"))
                .authorizeHttpRequests(request ->request.requestMatchers(HttpMethod.POST,apipath + apiVersion + "/clients" ).hasRole("USER"))
                .authorizeHttpRequests(request ->request.requestMatchers(HttpMethod.DELETE, apipath + apiVersion + "/clients/{id}").hasRole("ADMIN"))
                .authorizeHttpRequests(request ->request.requestMatchers(HttpMethod.GET,apipath + apiVersion + "/clients/me/profile" ).hasRole("CLIENT"))
                .authorizeHttpRequests(request ->request.requestMatchers(HttpMethod.PUT,apipath + apiVersion + "/clients/me/profile" ).hasRole("CLIENT"))
                .authorizeHttpRequests(request ->request.requestMatchers(HttpMethod.PATCH,apipath + apiVersion + "/clients/me/profile/dni" ).hasRole("CLIENT"))
                .authorizeHttpRequests(request ->request.requestMatchers(HttpMethod.PATCH,apipath + apiVersion + "/clients/me/profile/perfil" ).hasRole("CLIENT"))
                .authorizeHttpRequests(request ->request.requestMatchers(HttpMethod.DELETE,apipath + apiVersion + "/clients/me/profile" ).hasRole("CLIENT"))

                // MOVEMENTS
                .authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.GET, "/vivesbank" + apiVersion + "/movements").hasRole("ADMIN")) // GET ALL MOVEMENTS
                .authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.GET, "/vivesbank" + apiVersion + "/movements/client/{clientId}").hasRole( "ADMIN")) // GET MOVEMENTS BY CLIENT ID
                .authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.GET, "/vivesbank" + apiVersion + "/movements/tipo/{typeMovement}").hasRole("ADMIN")) // GET MOVEMENTS BY TYPE MOVEMENT
                .authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.POST, "/vivesbank" + apiVersion + "/movements").hasRole("ADMIN")) // CREATE MOVEMENT
                .authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.POST, "/vivesbank" + apiVersion + "/movements/{id}/reverse").hasRole("CLIENT")) // Reverse movement
                .authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.DELETE, "/vivesbank" + apiVersion + "/movements/{id}").hasRole("CLIENT")) // DELETE MOVEMENT BY ID

                //ACCOUNTS
                .authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.GET, "/vivesbank/" + apiVersion + "/accounts").hasAnyRole("CLIENT", "ADMIN"))
                .authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.GET, "/vivesbank/" + apiVersion + "/accounts/{id}").hasAnyRole("CLIENT", "ADMIN"))
                .authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.POST, "/vivesbank/" + apiVersion + "/accounts").hasAnyRole("CLIENT"))
                .authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.DELETE, "/vivesbank/" + apiVersion + "/accounts/{id}").hasAnyRole("CLIENT"))

                //API FRANKFURTER
                .authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.GET, "/currency").permitAll()) // Permitir acceso público a todas las monedas
                .authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.GET, "/currency/{symbol}").hasAnyRole("USER", "ADMIN")) // Detalles de moneda por símbolo
                .authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.GET, "/currency/history/{date}").hasAnyRole("USER", "ADMIN")) // Historial de moneda
                .authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.GET, "/currency/convert").hasAnyRole("USER", "ADMIN")) // Conversión de moneda
                .authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.GET, "/currency/timeseries").hasAnyRole("USER", "ADMIN")) // Series temporales
                .authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.GET, "/currency/latest").permitAll()) // Últimos tipos de cambio
                .authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.GET, "/currency/currencies").permitAll())
                .authorizeHttpRequests(request ->request.requestMatchers("/api/creditcard/**").permitAll())
                .authorizeHttpRequests(request ->request.requestMatchers("/api/products/**").permitAll())

                .authenticationProvider(authenticationProvider()).addFilterBefore(
                        authenticationFilter, UsernamePasswordAuthenticationFilter.class);;
        return http.build();
    }
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
