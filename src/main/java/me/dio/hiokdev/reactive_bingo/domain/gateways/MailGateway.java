package me.dio.hiokdev.reactive_bingo.domain.gateways;

import me.dio.hiokdev.reactive_bingo.domain.dto.MailMessage;
import reactor.core.publisher.Mono;

public interface MailGateway {

    Mono<Void> send(MailMessage mailMessage);

}
