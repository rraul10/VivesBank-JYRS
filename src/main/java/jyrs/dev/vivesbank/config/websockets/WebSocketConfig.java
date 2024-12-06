package jyrs.dev.vivesbank.config.websockets;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Value("${api.version}")
    private String apiVersion;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketBankAccountHandler(), "/ws/" + apiVersion + "/cuentas");
    }

    @Bean
    public WebSocketHandler webSocketBankAccountHandler() {
        return new WebSocketHandler("cuentas");
    }

    @Bean
    public WebSocketHandler webSocketUserHandler() {
        return new WebSocketHandler("users");
    }

    @Bean
    public WebSocketHandler webSocketMovementsHandler() {
        return new WebSocketHandler("movements");
    }

}