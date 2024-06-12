package me.dio.hiokdev.reactive_bingo.domain.models;

import me.dio.hiokdev.reactive_bingo.core.factory.domain.RoundFactory;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.RecursionException;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
public class BingoCardTest {

    @Test
    void whenGenerateThenThrowRecursionException() {
        var round = RoundFactory.builder().build();
        assertThrows(RecursionException.class, () -> RoundFactory.generateCards(50, round));
    }

}
