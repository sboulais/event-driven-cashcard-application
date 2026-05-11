package example.cashcard.sink;

import example.cashcard.CashcardSinkApplication;
import example.cashcard.domain.*;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = CashcardSinkApplication.class)
@Import({TestChannelBinderConfiguration.class})
@ExtendWith(OutputCaptureExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CashcardTransactionSinkIntegrationTest {

    @Test
    void should_PrintEnrichedTransactionToConsole_When_SinkReceivesMessage(
            @Autowired InputDestination inputDestination,
            CapturedOutput capturedOutput
    ) {
        // given
        Transaction transaction = new Transaction(1L,
                new Cashcard(101L, "Cecillia", 318.0)
        );

        EnrichedTransaction expected = new EnrichedTransaction(
                transaction.id(),
                transaction.cashcard(),
                ApprovalStatus.APPROVED,
                new CardHolderData(
                        null,
                        transaction.cashcard().owner(),
                        "10 rue pasteur"
                )
        );

        // when
        Message<EnrichedTransaction> message = MessageBuilder.withPayload(expected).build();
        inputDestination.send(message, "enrichTransaction-out-0");

        // then
        assertThat(capturedOutput.getOut())
                .contains(String.valueOf(expected.cashcard().id()))
                .contains(expected.cashcard().owner())
                .contains(String.valueOf(expected.cashcard().amountRequestedForAuth()))
                .contains(expected.approvalStatus().toString())
                .contains(expected.cardHolderData().address());
    }

    @Test
    void should_PrintEnrichedTransactionToFile_When_SinkReceivesMessage(
            @Autowired InputDestination inputDestination,
            CapturedOutput capturedOutput
    ) throws IOException {
        // given
        Transaction transaction = new Transaction(1L,
                new Cashcard(102L, "Sylvain", 246.01)
        );

        EnrichedTransaction expected = new EnrichedTransaction(
                transaction.id(),
                transaction.cashcard(),
                ApprovalStatus.APPROVED,
                new CardHolderData(
                        null,
                        transaction.cashcard().owner(),
                        "13 rue camille lacoste"
                )
        );

        // when
        Message<EnrichedTransaction> message = MessageBuilder.withPayload(expected).build();
        inputDestination.send(message, "enrichTransaction-out-0");
        Awaitility.await().until(() -> Files.exists(Path.of("transactions.log")));
        List<String> lines = Files.readAllLines(Path.of("transactions.log"));

        // then
        assertThat(lines.get(0))
                .contains(String.valueOf(expected.cashcard().id()))
                .contains(expected.cashcard().owner())
                .contains(String.valueOf(expected.cashcard().amountRequestedForAuth()))
                .contains(expected.approvalStatus().toString())
                .contains(expected.cardHolderData().address());
    }
}