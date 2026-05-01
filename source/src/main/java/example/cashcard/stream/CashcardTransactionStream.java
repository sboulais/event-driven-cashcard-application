package example.cashcard.stream;

import example.cashcard.domain.Transaction;
import example.cashcard.service.DataSourceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Supplier;

@Configuration
public class CashcardTransactionStream {
    @Bean
    Supplier<Transaction> approvalRequest(DataSourceService ds) {
        return ds::getData;
    }
}
