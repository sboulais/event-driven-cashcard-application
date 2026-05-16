package example.cashcard.e2e.test;

import example.cashcard.controller.CashCardController;
import example.cashcard.domain.Cashcard;
import example.cashcard.domain.Transaction;
import example.cashcard.enricher.CashcardTransactionEnricher;
import example.cashcard.ondemand.CashcardTransactionOnDemand;
import example.cashcard.service.EnrichementService;
import example.cashcard.sink.CashcardTransactionSink;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = CashcardTransactionOnDemandE2ETests.StreamTestConfig.class,
        properties = {
                "cashcard.datasource.flow.enabled=false",
                "spring.cloud.function.definition=enrichTransaction;sinkToFile"
        })
@EmbeddedKafka
public class CashcardTransactionOnDemandE2ETests {

    private RestTestClient client;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @EnableAutoConfiguration
    @Import({
            EnrichementService.class,
            CashCardController.class,
            CashcardTransactionOnDemand.class,
            CashcardTransactionEnricher.class,
            CashcardTransactionSink.class
    })
    public static class StreamTestConfig {

    }

    @BeforeEach
    void setUp() throws IOException {
        Files.deleteIfExists(Path.of("transactions.log"));
        client = RestTestClient.bindToApplicationContext(webApplicationContext)
                .build();
    }

    @Test
    @Disabled("Fonctionne depuis IntelliJ mais pas depuis la ligne de commande Maven.")
    public void should_PrintEnrichedTransactionToFile_When_SinkReceivesOnDemandMessage() throws IOException {

        // given
        Transaction expected = new Transaction(1L,
                new Cashcard(1L, "Marion", 12.87)
        );

        client.post()
                .uri("/publish/txn")
                .body(expected)
                .exchange()
                .returnResult();

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
