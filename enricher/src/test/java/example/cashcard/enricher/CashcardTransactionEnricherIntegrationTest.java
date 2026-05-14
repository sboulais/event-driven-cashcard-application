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
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

// Démarre le contexte Spring
@SpringBootTest(classes = CashcardEnricherApplication.class)
// Remplace le binder réel (Kafka/Rabbit) par un binder en mémoire :
@Import({TestChannelBinderConfiguration.class})
// Pour obtenir un nouveau context après chaque test :
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CashcardTransactionEnricherIntegrationTest {

    @MockitoBean
    private EnrichementService enrichementService;

    @Test
    void should_EmitEnrichedTransaction_When_ReceivingTransactionOnApprovalRequestDestination(
            @Autowired InputDestination inputDestination,
            @Autowired OutputDestination outputDestination
    ) throws IOException {

        // given
        Transaction transaction = new Transaction(1L,
                new Cashcard(1L, "Issam", 318.0)
        );

        EnrichedTransaction expected = new EnrichedTransaction(
                transaction.id(),
                transaction.cashcard(),
                ApprovalStatus.APPROVED,
                new CardHolderData(null, transaction.cashcard().owner(), "Saint-Germain")
        );

        given(enrichementService.enrichTransaction(transaction)).willReturn(expected);

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