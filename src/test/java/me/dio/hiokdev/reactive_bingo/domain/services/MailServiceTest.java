package me.dio.hiokdev.reactive_bingo.domain.services;

import me.dio.hiokdev.reactive_bingo.core.factory.domain.RoundFactory;
import me.dio.hiokdev.reactive_bingo.domain.dto.MailMessage;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.ReactiveBingoException;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.RetryException;
import me.dio.hiokdev.reactive_bingo.domain.gateways.MailGateway;
import me.dio.hiokdev.reactive_bingo.domain.models.BingoCard;
import me.dio.hiokdev.reactive_bingo.domain.models.Round;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class MailServiceTest {

    @Mock
    private MailGateway mailGateway;

    @InjectMocks
    private MailService mailService;

    @Test
    void whenSendThenReturnVoid() {
        when(mailGateway.send(any(MailMessage.class))).thenReturn(Mono.empty());
        MailMessage mailMessage = createMailMessage();

        StepVerifier.create(mailService.send(mailMessage))
                .verifyComplete();
        verify(mailGateway).send(any(MailMessage.class));
    }

    @Test
    void whenSendThenThrowRetryException() {
        when(mailGateway.send(any(MailMessage.class))).thenReturn(Mono.error(new RetryException("", new Exception(""))));
        MailMessage mailMessage = createMailMessage();

        StepVerifier.create(mailService.send(mailMessage))
                .verifyError(RetryException.class);
        verify(mailGateway).send(any(MailMessage.class));
    }

    @Test
    void whenSendThenThrowReactiveBingoException() {
        when(mailGateway.send(any(MailMessage.class))).thenReturn(Mono.error(new ReactiveBingoException("", new Exception(""))));
        MailMessage mailMessage = createMailMessage();

        StepVerifier.create(mailService.send(mailMessage))
                .verifyError(ReactiveBingoException.class);
        verify(mailGateway).send(any(MailMessage.class));
    }

    private static MailMessage createMailMessage() {
        Round round = RoundFactory.builder().build();
        round = RoundFactory.generateCards(10, round);
        round = RoundFactory.generateSortedNumbers(30, round);
        BingoCard bingoCard = round.bingoCards().get(0);
        return MailMessage.create(round, bingoCard.player(), bingoCard);
    }

}
