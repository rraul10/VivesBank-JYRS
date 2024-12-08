package jyrs.dev.vivesbank;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableCaching
@EnableJpaAuditing
@Slf4j
public class VivesBankApplication implements CommandLineRunner {

    @Value("${spring.profiles.active}")
    private String perfil;

    @Value("${server.port}")
    private String port;

    public static void main(String[] args) {
        SpringApplication.run(VivesBankApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🟢 Servidor escuchando en puerto: " + port + " y perfil: " + perfil);
    }
}
