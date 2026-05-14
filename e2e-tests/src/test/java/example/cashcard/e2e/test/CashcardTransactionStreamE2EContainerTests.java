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
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.kafka.KafkaContainer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@SpringBootTest(classes = CashcardTransactionStreamE2EContainerTests.StreamTestConfig.class)
public class CashcardTransactionStreamE2EContainerTests {

    @MockitoBean
    private DataSourceService dataSourceService;

    @EnableAutoConfiguration
    @Import({
            CashcardTransactionStream.class,
            CashcardTransactionEnricher.class,
            CashcardTransactionSink.class,
            EnrichementService.class})
    public static class StreamTestConfig {
        @Bean
        @ServiceConnection
        KafkaContainer kafkaContainer() {
            KafkaContainer kafka = new KafkaContainer("apache/kafka-native:3.8.0");
            kafka.start();
            return kafka;
        }
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