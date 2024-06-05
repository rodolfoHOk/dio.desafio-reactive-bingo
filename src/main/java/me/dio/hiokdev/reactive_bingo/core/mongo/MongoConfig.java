package me.dio.hiokdev.reactive_bingo.core.mongo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.List;

@Configuration
@EnableMongoAuditing(dateTimeProviderRef = "dateTimeProvider")
public class MongoConfig {

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        var converters = List.of(new OffsetDateTimeToDateConverter(), new DateToOffsetDateTimeConverter());
        return new MongoCustomConversions(converters);
    }

}
