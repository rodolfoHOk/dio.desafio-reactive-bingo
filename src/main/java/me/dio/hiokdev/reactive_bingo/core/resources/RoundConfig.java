package me.dio.hiokdev.reactive_bingo.core.resources;

import me.dio.hiokdev.reactive_bingo.application.mappers.BingoCardMapper;
import me.dio.hiokdev.reactive_bingo.application.mappers.RoundMapper;
import me.dio.hiokdev.reactive_bingo.application.ports.RoundUseCases;
import me.dio.hiokdev.reactive_bingo.application.usecases.RoundUseCasesImpl;
import me.dio.hiokdev.reactive_bingo.domain.gateways.RoundGateway;
import me.dio.hiokdev.reactive_bingo.domain.services.MailService;
import me.dio.hiokdev.reactive_bingo.domain.services.RoundService;
import me.dio.hiokdev.reactive_bingo.domain.services.query.PlayerQueryService;
import me.dio.hiokdev.reactive_bingo.domain.services.query.RoundQueryService;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.adapters.RoundGatewayImpl;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.mappers.RoundDocumentMapper;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.repositories.RoundDocumentRepository;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.repositories.impl.FindOnDemandRoundRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoundConfig {

    @Bean
    public RoundGateway roundGateway(
            final RoundDocumentRepository roundDocumentRepository,
            final FindOnDemandRoundRepositoryImpl findOnDemandRoundRepository,
            final RoundDocumentMapper roundDocumentMapper
    ) {
        return new RoundGatewayImpl(roundDocumentRepository, findOnDemandRoundRepository, roundDocumentMapper);
    }

    @Bean
    public RoundQueryService roundQueryService(final RoundGateway roundGateway) {
        return new RoundQueryService(roundGateway);
    }

    @Bean
    public RoundService roundService(
            final PlayerQueryService playerQueryService,
            final RoundQueryService roundQueryService,
            final RoundGateway roundGateway,
            final MailService mailService
            ) {
        return new RoundService(playerQueryService, roundQueryService, roundGateway, mailService);
    }

    @Bean
    public RoundUseCases roundUseCases(
            final RoundService roundService,
            final RoundQueryService roundQueryService,
            final RoundMapper roundMapper,
            final BingoCardMapper bingoCardMapper
    ) {
        return new RoundUseCasesImpl(roundService, roundQueryService, roundMapper, bingoCardMapper);
    }

}
