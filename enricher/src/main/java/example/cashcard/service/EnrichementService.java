package example.cashcard.service;

import example.cashcard.domain.ApprovalStatus;
import example.cashcard.domain.CardHolderData;
import example.cashcard.domain.EnrichedTransaction;
import example.cashcard.domain.Transaction;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EnrichementService {
    public EnrichedTransaction enrichTransaction(Transaction transaction) {
        return new EnrichedTransaction(
                transaction.id(),
                transaction.cashcard(),
                ApprovalStatus.APPROVED,
                new CardHolderData(
                        UUID.randomUUID(),
                        transaction.cashcard().owner(),
                        "5 impasse des muriers")
        );
    }
}
