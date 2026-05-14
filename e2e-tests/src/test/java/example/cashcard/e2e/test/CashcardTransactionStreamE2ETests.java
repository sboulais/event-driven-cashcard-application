package example.cashcard.e2e.test;

import example.cashcard.domain.Cashcard;
import example.cashcard.domain.Transaction;
import example.cashcard.enricher.CashcardTransactionEnricher;
import example.cashcard.service.DataSourceService;
import example.cashcard.service.EnrichementService;
import example.cashcard.sink.CashcardTransactionSink;
import example.cashcard.stream.CashcardTransactionStream;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

// Le paramètre classes dans @SpringBootTest permet de spécifier explicitement quelle
// classe de configuration utiliser pour construire le contexte Spring, au lieu de laisser
// Spring Boot scanner automatiquement le classpath. Cela donne un contrôle fin sur les
// composants chargés pendant le test.
@SpringBootTest(classes = CashcardTransactionStreamE2ETests.StreamTestConfig.class)
// Utilise le broker Kafka embarqué dans Spring
@EmbeddedKafka
public class CashcardTransactionStreamE2ETests {

    @MockitoBean
    private DataSourceService dataSourceService;

    @EnableAutoConfiguration
    @Import({
            CashcardTransactionStream.class,
            CashcardTransactionEnricher.class,
            CashcardTransactionSink.class,
            EnrichementService.class})
    public static class StreamTestConfig {

    }

    @BeforeEach
    public void cleanUp() throws IOException {
        Files.deleteIfExists(Path.of("transactions.log"));
    }

    @Test
    public void should_PrintEnrichedTransactionToConsole_When_SinkReceiveStreamMessage() throws IOException {

        // given
        Transaction expected = new Transaction(Long.valueOf(1),
                new Cashcard(Long.valueOf(1), "Sébastien", 187.55)
        );

        given(dataSourceService.getData()).willReturn(expected);

        // when
        Awaitility.await().until(() -> Files.exists(Path.of("transactions.log")));
        List<String> lines = Files.readAllLines(Path.of("transactions.log"));

        // then
        assertThat(lines.get(0))
                .contains(String.valueOf(expected.cashcard().id()))
                .contains(expected.cashcard().owner())
                .contains(String.valueOf(expected.cashcard().amountRequestedForAuth()));
    }
}
