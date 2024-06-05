package me.dio.hiokdev.reactive_bingo.infrastructure.persistence.repositories.impl;

import com.github.javafaker.Faker;
import me.dio.hiokdev.reactive_bingo.core.config.MongoDBTestConfig;
import me.dio.hiokdev.reactive_bingo.core.factory.FakerData;
import me.dio.hiokdev.reactive_bingo.core.factory.document.RoundDocumentFactory;
import me.dio.hiokdev.reactive_bingo.core.factory.domain.RoundFactory;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.documents.RoundDocument;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.repositories.RoundDocumentRepository;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.repositories.impl.FindOnDemandRoundRepositoryImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

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

}
