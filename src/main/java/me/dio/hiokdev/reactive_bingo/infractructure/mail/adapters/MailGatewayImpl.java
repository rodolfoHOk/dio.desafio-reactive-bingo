package me.dio.hiokdev.reactive_bingo.infractructure.mail.adapters;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import me.dio.hiokdev.reactive_bingo.core.retry.RetryHelper;
import me.dio.hiokdev.reactive_bingo.domain.dto.MailMessage;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.ReactiveBingoException;
import me.dio.hiokdev.reactive_bingo.domain.gateways.MailGateway;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class MailGatewayImpl implements MailGateway {

    private final String sender;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final RetryHelper retryHelper;

    @Override
    public Mono<Void> send(final MailMessage mailMessage) {
        return Mono.just(mailSender.createMimeMessage())
                .flatMap(mimeMessage -> buildMimeMessage(mimeMessage, mailMessage))
                .flatMap(this::sendWithRetry);
    }

    private Mono<MimeMessage> buildMimeMessage(final MimeMessage mimeMessage, final MailMessage mailMessage) {
        return Mono.fromCallable(() -> {
            try {
                var helper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());
                helper.setTo(mailMessage.destination());
                helper.setFrom(sender);
                helper.setSubject(mailMessage.subject());
                String body = buildMailBodyFromTemplate(mailMessage.template(), mailMessage.variables());
                helper.setText(body);
                return helper.getMimeMessage();
            } catch (MessagingException e) {
                throw new ReactiveBingoException(e.getMessage(), e);
            }
        });
    }

    private String buildMailBodyFromTemplate(final String template, final Map<String, Object> variables) {
        var context = new Context(Locale.of("pt", "BR"));
        context.setVariables(variables);
        return templateEngine.process(template, context);
    }

    private Mono<Void> sendWithRetry(final MimeMessage mimeMessage) {
        return Mono.fromCallable(() -> {
                    mailSender.send(mimeMessage);
                    return mimeMessage;
                })
                .retryWhen(retryHelper.processRetry(UUID.randomUUID().toString(),
                        throwable -> throwable instanceof MailException))
                .then();
    }

}
