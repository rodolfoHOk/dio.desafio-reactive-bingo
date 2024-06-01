package me.dio.hiokdev.reactive_bingo.domain.services;

import lombok.RequiredArgsConstructor;
import me.dio.hiokdev.reactive_bingo.domain.dto.MailMessage;
import me.dio.hiokdev.reactive_bingo.domain.gateways.MailGateway;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class MailService {

    private final MailGateway mailGateway;

    public Mono<Void> send(final MailMessage mailMessage) {
        return mailGateway.send(mailMessage);
    }

}
