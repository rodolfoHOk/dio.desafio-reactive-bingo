package me.dio.hiokdev.reactive_bingo.core.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import me.dio.hiokdev.reactive_bingo.core.mongo.DateToOffsetDateTimeConverter;
import me.dio.hiokdev.reactive_bingo.core.mongo.OffsetDateTimeProvider;
import me.dio.hiokdev.reactive_bingo.core.mongo.OffsetDateTimeToDateConverter;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.repositories.PlayerDocumentRepository;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.repositories.RoundDocumentRepository;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.repositories.impl.FindOnDemandPlayerRepositoryImpl;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.repositories.impl.FindOnDemandRoundRepositoryImpl;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.utils.QueryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.lang.NonNull;

import java.util.List;

@TestConfiguration
@EnableReactiveMongoRepositories(basePackageClasses = {PlayerDocumentRepository.class, RoundDocumentRepository.class})
public class MongoDBTestConfig extends AbstractReactiveMongoConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String uri;

    @NonNull
    @Override
    protected String getDatabaseName() {
        return "reactive-bingo-test";
    }

    @NonNull
    @Override
    public MongoClient reactiveMongoClient() {
        return MongoClients.create(uri);
    }

    @NonNull
    @Override
    public MongoCustomConversions customConversions() {
        var converters = List.of(new OffsetDateTimeToDateConverter(), new DateToOffsetDateTimeConverter());
        return new MongoCustomConversions(converters);
    }

    @Bean
    public DateTimeProvider dateTimeProvider() {
        return new OffsetDateTimeProvider();
    }

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate() {
        return new ReactiveMongoTemplate(reactiveMongoClient(), getDatabaseName());
    }

    @Bean
    public QueryBuilder queryBuilder() {
        return new QueryBuilder();
    }

    @Bean
    public FindOnDemandPlayerRepositoryImpl findOnDemandPlayerRepository() {
        return new FindOnDemandPlayerRepositoryImpl(reactiveMongoTemplate(), queryBuilder());
    }

    @Bean
    public FindOnDemandRoundRepositoryImpl findOnDemandRoundRepository() {
        return new FindOnDemandRoundRepositoryImpl(reactiveMongoTemplate(), queryBuilder());
    }

}
