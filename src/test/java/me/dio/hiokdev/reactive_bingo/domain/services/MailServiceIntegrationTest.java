package me.dio.hiokdev.reactive_bingo.domain.services;

import com.github.javafaker.Faker;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import me.dio.hiokdev.reactive_bingo.core.factory.FakerData;
import me.dio.hiokdev.reactive_bingo.core.factory.domain.RoundFactory;
import me.dio.hiokdev.reactive_bingo.core.retry.RetryConfig;
import me.dio.hiokdev.reactive_bingo.core.retry.RetryHelper;
import me.dio.hiokdev.reactive_bingo.domain.dto.MailMessage;
import me.dio.hiokdev.reactive_bingo.domain.gateways.MailGateway;
import me.dio.hiokdev.reactive_bingo.domain.models.BingoCard;
import me.dio.hiokdev.reactive_bingo.domain.models.Round;
import me.dio.hiokdev.reactive_bingo.infractructure.mail.adapters.MailGatewayImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;
import reactor.test.StepVerifier;

import java.util.Properties;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class MailServiceIntegrationTest {

    private static final Integer PORT = 8081;
    private static final String USER = "teste@teste.com.br";
    private static final String PASSWORD = "123456";

    private String sender;
    private GreenMail smtpServer;
    private MailService mailService;
    private final Faker faker = FakerData.getFaker();

    @BeforeEach
    void setup(ApplicationContext applicationContext) {
        this.smtpServer = createSmtpServer(PORT, USER, PASSWORD);
        this.sender = faker.internet().emailAddress();
        var host = smtpServer.getSmtp().getServerSetup().getBindAddress();
        JavaMailSender mailSender = createMailSender(host, PORT, USER, PASSWORD);
        TemplateEngine templateEngine = createTemplateEngine(applicationContext);
        RetryHelper retryHelper = new RetryHelper(new RetryConfig(1L, 1L));
        MailGateway mailGateway = new MailGatewayImpl(sender, mailSender, templateEngine, retryHelper);
        this.mailService = new MailService(mailGateway);
        this.smtpServer.start();
    }

    @AfterEach
    void tearDown() {
        this.smtpServer.stop();
    }

    @Test
    void whenSendToWinnerThenReturnVoid() throws MessagingException {
        Round round = RoundFactory.builder().build();
        round = RoundFactory.generateCards(10, round);
        round = RoundFactory.generateSortedNumbers(30, round);
        BingoCard bingoCard = round.bingoCards().get(0);
        MailMessage winnerMailMessage = MailMessage.createWinner(round, bingoCard.player(), bingoCard);

        StepVerifier.create(mailService.send(winnerMailMessage)).verifyComplete();
        assertThat(smtpServer.getReceivedMessages().length).isOne();
        var message = Stream.of(smtpServer.getReceivedMessages()).findFirst().orElseThrow();
        assertThat(message.getSubject()).isEqualTo(winnerMailMessage.subject());
        assertThat(message.getRecipients(Message.RecipientType.TO))
                .contains(new InternetAddress(winnerMailMessage.destination()));
        assertThat(message.getHeader("FROM")).contains(sender);
    }

    @Test
    void whenSendToNonWinnerThenReturnVoid() throws MessagingException {
        Round round = RoundFactory.builder().build();
        round = RoundFactory.generateCards(10, round);
        round = RoundFactory.generateSortedNumbers(30, round);
        BingoCard bingoCard = round.bingoCards().get(0);
        MailMessage mailMessage = MailMessage.create(round, bingoCard.player(), bingoCard);

        StepVerifier.create(mailService.send(mailMessage)).verifyComplete();
        assertThat(smtpServer.getReceivedMessages().length).isOne();
        var message = Stream.of(smtpServer.getReceivedMessages()).findFirst().orElseThrow();
        assertThat(message.getSubject()).isEqualTo(mailMessage.subject());
        assertThat(message.getRecipients(Message.RecipientType.TO))
                .contains(new InternetAddress(mailMessage.destination()));
        assertThat(message.getHeader("FROM")).contains(sender);
    }

    private GreenMail createSmtpServer(Integer port, String user, String password) {
        var smtpServer = new GreenMail(ServerSetupTest.SMTP.port(port));
        smtpServer.setUser(user, password);
        return smtpServer;
    }

    private JavaMailSenderImpl createMailSender(String host, Integer port, String user, String password) {
        var sender = new JavaMailSenderImpl();
        sender.setHost(host);
        sender.setPort(port);
        var mailProperties = new Properties();
        mailProperties.setProperty("mail.transport.protocol", "smtp");
        mailProperties.setProperty("mail.smtp.auth", "true");
        mailProperties.setProperty("mail.smtp.starttls.enable", "true");
        mailProperties.setProperty("mail.debug", "false");
        sender.setJavaMailProperties(mailProperties);
        sender.setUsername(user);
        sender.setPassword(password);
        return sender;
    }

    private SpringTemplateEngine createTemplateEngine(ApplicationContext applicationContext) {
        var templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setApplicationContext(applicationContext);

        var templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        templateEngine.setEnableSpringELCompiler(true);
        templateEngine.addDialect(new Java8TimeDialect());
        return templateEngine;
    }

}
