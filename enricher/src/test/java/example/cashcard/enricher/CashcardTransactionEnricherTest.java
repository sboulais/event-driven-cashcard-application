package example.cashcard.enricher;

import example.cashcard.domain.*;
import example.cashcard.service.EnrichementService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CashcardTransactionEnricherTest {

    @Test
    void should_ReturnApprovedEnrichedTransaction_When_TransactionIsValid() {

        // given
        EnrichementService enrichementService = new EnrichementService();

        Transaction transaction =
                new Transaction(4L, new Cashcard(7L, "Jean-Michel", 137.0));

        // when
        EnrichedTransaction actual = enrichementService.enrichTransaction(transaction);

        EnrichedTransaction expected = new EnrichedTransaction(
                transaction.id(),
                transaction.cashcard(),
                ApprovalStatus.APPROVED,
                new CardHolderData(
                        null,
                        transaction.cashcard().owner(),
                        "5 impasse des muriers"
                )
        );

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