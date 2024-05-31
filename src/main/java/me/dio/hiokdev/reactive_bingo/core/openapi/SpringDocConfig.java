package me.dio.hiokdev.reactive_bingo.core.openapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Reactive Bingo", description = "API reativa de jogo de Bingo", version = "1.0.0"),
        servers = {@Server(url = "http://localhost:8080/bingo-api", description = "local")}
)
public class SpringDocConfig {
}
