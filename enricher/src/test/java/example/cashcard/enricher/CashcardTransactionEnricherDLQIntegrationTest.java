package example.cashcard.enricher;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.cashcard.CashcardEnricherApplication;
import example.cashcard.domain.*;
import example.cashcard.service.EnrichementService;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest(
        classes = CashcardEnricherApplication.class,
        properties = {
                "cashcard.datasource.flow.enabled=false",
                "spring.cloud.function.definition=enrichTransaction;sinkToFile"
        })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@EmbeddedKafka
class CashcardTransactionEnricherDLQIntegrationTest {

    @MockitoBean
    private EnrichementService enrichementService;

    @Autowired
    private KafkaTemplate<String, byte[]> kafkaTemplate;

    @Autowired
    private ConsumerFactory<String, byte[]> consumerFactory;

    @Test
    void should_PublishToDLQ_When_EnricherThrowException() throws IOException {

        // given
        Transaction transaction = new Transaction(16L,
                new Cashcard(1L, "Raphaelle", 15.0)
        );

        // Change le comportement de enrichTransaction pour simuler une exception
        given(enrichementService.enrichTransaction(any()))
                .willThrow(new RuntimeException("Enrichment failed !"));

        // when
        // Créer un consommateur Kafka et abonnement au topic "enrichTransaction.DLQ"
        Consumer<String, byte[]> consumer = consumerFactory.createConsumer();
        consumer.subscribe(List.of("enrichTransaction.DLQ"));

        // Convertit la transaction en bytes et l'envoie sur le topic "enrichTransaction-in-0"
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] payload = objectMapper.writeValueAsBytes(transaction);
        kafkaTemplate.send("approvalRequest-out-0", payload);

        // Attend un message arrivant sur le topic
        List<ConsumerRecord<String, byte[]>> received = new ArrayList<>();
        Awaitility
                .await()
                .atMost(30, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .until(() -> {
                    consumer.poll(Duration.ofMillis(500))
                            .forEach(received::add);
                    return !received.isEmpty();
                });

        // then
        assertThat(received).isNotEmpty();
    }
}