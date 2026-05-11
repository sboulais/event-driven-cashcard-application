package example.cashcard.sink;

import example.cashcard.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
public class CashcardTransactionSinkTest {
    @Test
    void should_PrintEnrichedTransactionToConsole_When_PrintExpectedMessage(CapturedOutput capturedOutput) {

        // given
        Transaction transaction =
                new Transaction(13L, new Cashcard(7L, "Christophe", 278.12));

        EnrichedTransaction expected = new EnrichedTransaction(
                transaction.id(),
                transaction.cashcard(),
                ApprovalStatus.APPROVED,
                new CardHolderData(
                        null,
                        transaction.cashcard().owner(),
                        "1 place saint-pierre"
                )
        );

        // when
        System.out.println(expected);

        // then
        assertThat(capturedOutput.getOut())
                .contains(String.valueOf(expected.cashcard().id()))
                .contains(expected.cashcard().owner())
                .contains(expected.approvalStatus().toString())
                .contains(expected.cardHolderData().address());
    }
}
