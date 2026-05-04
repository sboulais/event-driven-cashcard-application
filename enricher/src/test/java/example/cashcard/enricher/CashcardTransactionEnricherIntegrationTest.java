package example.cashcard.enricher;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.cashcard.CashcardEnricherApplication;
import example.cashcard.domain.*;
import example.cashcard.service.EnrichementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

// Démarre le contexte Spring
@SpringBootTest(classes = CashcardEnricherApplication.class)
// Remplace le binder réel (Kafka/Rabbit) par un binder en mémoire :
@Import({TestChannelBinderConfiguration.class})
// Pour obtenir un nouveau context après chaque test :
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CashcardTransactionEnricherIntegrationTest {

    @Test
    void should_EmitEnrichedTransaction_When_ReceivingTransactionOnApprovalRequestDestination(
            @Autowired InputDestination inputDestination,
            @Autowired OutputDestination outputDestination,
            @Autowired EnrichementService enrichementService
    ) throws IOException {

        // given
        Transaction transaction = new Transaction(1L,
                new Cashcard(1L, "Issam", 318.0)
        );

        EnrichedTransaction expected = enrichementService.enrichTransaction(transaction);

        // when
        Message<Transaction> message = MessageBuilder.withPayload(transaction).build();
        inputDestination.send(message, "approvalRequest-out-0");
        Message<byte[]> result = outputDestination.receive(5000, "enrichTransaction-out-0");
        ObjectMapper objectMapper = new ObjectMapper();
        EnrichedTransaction actual = objectMapper.readValue(result.getPayload(), EnrichedTransaction.class);

        // then
        assertAll(
                () -> assertEquals(actual.id(), expected.id()),
                () -> assertEquals(actual.cashcard(), expected.cashcard()),
                () -> assertEquals(actual.approvalStatus(), expected.approvalStatus()),
                () -> assertEquals(actual.cardHolderData().name(), expected.cardHolderData().name()),
                () -> assertEquals(actual.cardHolderData().address(), expected.cardHolderData().address())
        );
    }
}