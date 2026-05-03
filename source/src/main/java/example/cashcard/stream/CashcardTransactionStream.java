package example.cashcard.stream;

import example.cashcard.domain.Transaction;
import example.cashcard.service.DataSourceService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Supplier;

@Configuration
public class CashcardTransactionStream {

    // topic par défaut :
    // - approvalRequest-in-0
    // - approvalRequest-out-0
    @Bean
    @ConditionalOnProperty(
            name = "cashcard.datasource.flow.enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    Supplier<Transaction> approvalRequest(DataSourceService ds) {
        return ds::getData;
    }
}
