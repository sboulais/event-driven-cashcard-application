package example.cashcard.ondemand;

import example.cashcard.domain.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CashcardTransactionOnDemand {

    private StreamBridge streamBridge;

    public CashcardTransactionOnDemand(@Autowired StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public void publishTransaction(final Transaction transaction) {
        streamBridge.send("approvalRequest-out-1", transaction);
    }
}
