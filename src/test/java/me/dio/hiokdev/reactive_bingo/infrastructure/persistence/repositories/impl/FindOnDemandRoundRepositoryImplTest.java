package me.dio.hiokdev.reactive_bingo.infrastructure.persistence.repositories.impl;

import com.github.javafaker.Faker;
import me.dio.hiokdev.reactive_bingo.core.config.MongoDBTestConfig;
import me.dio.hiokdev.reactive_bingo.core.factory.FakerData;
import me.dio.hiokdev.reactive_bingo.core.factory.document.RoundDocumentFactory;
import me.dio.hiokdev.reactive_bingo.core.factory.domain.RoundFactory;
import me.dio.hiokdev.reactive_bingo.domain.dto.PageableRounds;
import me.dio.hiokdev.reactive_bingo.domain.enums.RoundsSortBy;
import me.dio.hiokdev.reactive_bingo.domain.enums.SortDirection;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.documents.RoundDocument;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.repositories.RoundDocumentRepository;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.repositories.impl.FindOnDemandRoundRepositoryImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MongoDBTestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FindOnDemandRoundRepositoryImplTest {

    @Autowired
    private RoundDocumentRepository roundDocumentRepository;
    @Autowired
    private FindOnDemandRoundRepositoryImpl findOnDemandRoundRepository;
    private static final Faker faker = FakerData.getFaker();
    private final List<RoundDocument> storedDocuments = new ArrayList<>();

    @BeforeEach
    void setup() {
        var rounds = Stream.generate(() -> RoundFactory.builder().randomState().build()).limit(15).toList();
        var roundsDocuments = rounds.stream().map(round -> RoundDocumentFactory.builder(round).build()).toList();
        var savedRounds = roundDocumentRepository.saveAll(roundsDocuments).collectList().block();
        storedDocuments.addAll(Objects.requireNonNull(savedRounds));
    }

    @AfterEach
    void tearDown() {
        roundDocumentRepository.deleteAll().block();
        storedDocuments.clear();
    }

    @Test
    void whenFindOnDemandFilterBySentenceThenReturnRounds() {
        var selectRandomDocument = storedDocuments.get(faker.number().numberBetween(0, storedDocuments.size()));
        var sentence = selectRandomDocument.state().name();
        var pageable = PageableRounds.builder().sentence(sentence).build();

        StepVerifier.create(findOnDemandRoundRepository.findOnDemand(pageable))
                .recordWith(ArrayList::new)
                .thenConsumeWhile(actual -> true)
                .consumeRecordedWith(actual -> {
                    var expectedSize = storedDocuments.stream()
                            .filter(document -> document.state().name().equals(sentence))
                            .count();
                    assertThat(actual.size()).isEqualTo(expectedSize);
                    var actualList = new ArrayList<>(actual);
                    assertThat(actualList).isSortedAccordingTo(Comparator.comparing(RoundDocument::createdAt).reversed());
                })
                .verifyComplete();
    }

    @Test
    void whenCountFilterBySentenceThenReturnInteger() {
        var selectRandomDocument = storedDocuments.get(faker.number().numberBetween(0, storedDocuments.size()));
        var sentence = selectRandomDocument.state().name();
        var pageable = PageableRounds.builder().sentence(sentence).build();

        StepVerifier.create(findOnDemandRoundRepository.count(pageable))
                .assertNext(actual -> {
                    var expectedSize = storedDocuments.stream()
                            .filter(document -> document.state().name().equals(sentence))
                            .count();
                    assertThat(actual).isEqualTo(expectedSize);
                })
                .verifyComplete();
    }

    private static Stream<Arguments> whenFindOnDemandSortByThenReturnPlayersInOrder() {
        return Stream.of(
                Arguments.of(
                        PageableRounds.builder().sortBy(RoundsSortBy.CREATE_DATE).sortDirection(SortDirection.DESC).build(),
                        Comparator.comparing(RoundDocument::createdAt).reversed()
                ),
                Arguments.of(
                        PageableRounds.builder().sortBy(RoundsSortBy.CREATE_DATE).sortDirection(SortDirection.ASC).build(),
                        Comparator.comparing(RoundDocument::createdAt)
                ),
                Arguments.of(
                        PageableRounds.builder().sortBy(RoundsSortBy.STATE).sortDirection(SortDirection.DESC).build(),
                        Comparator.comparing(RoundDocument::state).reversed()
                ),
                Arguments.of(
                        PageableRounds.builder().sortBy(RoundsSortBy.STATE).sortDirection(SortDirection.ASC).build(),
                        Comparator.comparing(RoundDocument::state)
                )
        );
    }

    @MethodSource
    @ParameterizedTest
    void whenFindOnDemandSortByThenReturnPlayersInOrder(final PageableRounds pageable, final Comparator<RoundDocument> comparator) {
        StepVerifier.create(findOnDemandRoundRepository.findOnDemand(pageable))
                .recordWith(ArrayList::new)
                .thenConsumeWhile(actual -> true)
                .consumeRecordedWith(actual -> {
                    var actualList = new ArrayList<>(actual);
                    System.out.println(actualList);
                    assertThat(actualList).isSortedAccordingTo(comparator);
                })
                .verifyComplete();
    }

}
