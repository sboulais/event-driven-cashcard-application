package example.cashcard.stream;

import example.cashcard.domain.Transaction;
import example.cashcard.service.DataSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Supplier;

@Configuration
public class CashCardStream {

    private StreamBridge streamBridge;

    public CashCardStream(@Autowired StreamBridge sb) {
        streamBridge = sb;
    }

    @Bean
    Supplier<Transaction> approvalRequest(DataSourceService ds) {
        return ds::getData;
    }

    public void publishTransaction(final Transaction transaction) {
        streamBridge.send("approvalRequest-out-0", transaction);
    }
}
