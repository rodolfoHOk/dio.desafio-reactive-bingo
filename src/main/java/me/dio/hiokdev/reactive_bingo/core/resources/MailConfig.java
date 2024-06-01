package me.dio.hiokdev.reactive_bingo.core.resources;

import me.dio.hiokdev.reactive_bingo.core.retry.RetryHelper;
import me.dio.hiokdev.reactive_bingo.domain.gateways.MailGateway;
import me.dio.hiokdev.reactive_bingo.domain.services.MailService;
import me.dio.hiokdev.reactive_bingo.infractructure.mail.adapters.MailGatewayImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;

@Configuration
public class MailConfig {

    @Value("${reactive-bingo.mail.sender}")
    private String sender;

    @Bean
    public MailGateway mailGateway(
            final JavaMailSender mailSender,
            final TemplateEngine templateEngine,
            final RetryHelper retryHelper
    ) {
        return new MailGatewayImpl(sender, mailSender, templateEngine, retryHelper);
    }

    @Bean
    public MailService mailService(MailGateway mailGateway) {
        return new MailService(mailGateway);
    }

}
