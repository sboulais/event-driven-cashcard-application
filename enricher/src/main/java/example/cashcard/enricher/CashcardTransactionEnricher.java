package example.cashcard.enricher;

import example.cashcard.domain.EnrichedTransaction;
import example.cashcard.domain.Transaction;
import example.cashcard.service.EnrichementService;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class CashcardTransactionEnricher {
    public Function<Transaction, EnrichedTransaction> enrichTransaction
            (EnrichementService enrichementService) {
        return enrichementService::enrichTransaction;
    }
}
