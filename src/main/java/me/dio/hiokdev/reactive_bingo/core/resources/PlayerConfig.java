package me.dio.hiokdev.reactive_bingo.core.resources;

import me.dio.hiokdev.reactive_bingo.application.mappers.PlayerMapper;
import me.dio.hiokdev.reactive_bingo.application.ports.PlayerUseCases;
import me.dio.hiokdev.reactive_bingo.application.usecases.PlayerUseCasesImpl;
import me.dio.hiokdev.reactive_bingo.domain.gateways.PlayerGateway;
import me.dio.hiokdev.reactive_bingo.domain.services.PlayerService;
import me.dio.hiokdev.reactive_bingo.domain.services.query.PlayerQueryService;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.adapters.PlayerGatewayImpl;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.mappers.PlayerDocumentMapper;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.repositories.PlayerDocumentRepository;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.repositories.impl.FindOnDemandPlayerRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlayerConfig {

    @Bean
    public PlayerGateway playerGateway(
            final PlayerDocumentRepository playerDocumentRepository,
            final FindOnDemandPlayerRepositoryImpl findOnDemandPlayerRepository,
            final PlayerDocumentMapper playerDocumentMapper
    ) {
        return new PlayerGatewayImpl(playerDocumentRepository, findOnDemandPlayerRepository, playerDocumentMapper);
    }

    @Bean
    PlayerQueryService playerQueryService(final PlayerGateway playerGateway) {
        return new PlayerQueryService(playerGateway);
    }

    @Bean
    PlayerService playerService(final PlayerQueryService playerQueryService, final PlayerGateway playerGateway) {
        return new PlayerService(playerQueryService, playerGateway);
    }

    @Bean
    public PlayerUseCases playerUseCases(
            final PlayerService playerService,
            final PlayerQueryService playerQueryService,
            final PlayerMapper playerMapper
    ) {
        return new PlayerUseCasesImpl(playerService, playerQueryService, playerMapper);
    }

}
