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

    public SecurityConfig(UserDetailsService userService, JwtAuthenticationFilter authenticationFilter) {
        this.userService = userService;
        this.authenticationFilter = authenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(manager ->manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request ->request.requestMatchers("/error/**").permitAll())
                .authorizeHttpRequests(request -> request.requestMatchers("/vivesbank/" + apiVersion + "/auth/**").permitAll())
                .authorizeHttpRequests(request ->request.requestMatchers(HttpMethod.GET,"/vivesbank/" + apiVersion + "/users" ).hasRole("ADMIN"))
                .authorizeHttpRequests(request ->request.requestMatchers(HttpMethod.GET, "/vivesbank/" + apiVersion + "/users/{id}").hasRole("ADMIN"))
                .authorizeHttpRequests(request ->request.requestMatchers(HttpMethod.GET, "/vivesbank/" + apiVersion + "/users/users/name/{name}").hasRole("ADMIN"))
                .authorizeHttpRequests(request ->request.requestMatchers(HttpMethod.POST, "/vivesbank/" + apiVersion + "/users").permitAll())
                .authorizeHttpRequests(request ->request.requestMatchers(HttpMethod.PUT, "/vivesbank/" + apiVersion + "/users/{id}").hasAnyRole("ADMIN", "USER"))
                .authorizeHttpRequests(request ->request.requestMatchers(HttpMethod.DELETE, "/vivesbank/" + apiVersion + "/users/{id}").hasAnyRole("ADMIN", "USER"))
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
