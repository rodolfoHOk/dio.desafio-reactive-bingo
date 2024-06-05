package me.dio.hiokdev.reactive_bingo;

import me.dio.hiokdev.reactive_bingo.core.retry.RetryConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;

@SpringBootApplication
@EnableReactiveMongoAuditing(dateTimeProviderRef = "dateTimeProvider")
@ConfigurationPropertiesScan(basePackageClasses = {RetryConfig.class})
public class ReactiveBingoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactiveBingoApplication.class, args);
    }

}
