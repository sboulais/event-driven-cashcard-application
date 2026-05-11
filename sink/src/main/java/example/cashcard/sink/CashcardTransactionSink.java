package example.cashcard.sink;

import example.cashcard.domain.EnrichedTransaction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.StringJoiner;
import java.util.function.Consumer;

@Configuration
public class CashcardTransactionSink {
    @Bean
    public Consumer<EnrichedTransaction> sinkToConsole() {
        return enrichedTransaction -> {
            System.out.println("Transaction Received: " + enrichedTransaction);
        };
    }

    @Bean
    public Consumer<EnrichedTransaction> sinkToFile() {
        return enrichedTransaction -> {
            StringJoiner line = new StringJoiner(",");
            line
                    .add(String.valueOf(enrichedTransaction.id()))
                    .add(String.valueOf(enrichedTransaction.cashcard().id()))
                    .add(String.valueOf(enrichedTransaction.cashcard().amountRequestedForAuth()))
                    .add(String.valueOf(enrichedTransaction.cardHolderData().name()))
                    .add(String.valueOf(enrichedTransaction.cardHolderData().userId()))
                    .add(String.valueOf(enrichedTransaction.cardHolderData().address()))
                    .add(String.valueOf(enrichedTransaction.approvalStatus()));
            try {
                new File("transactions.log").createNewFile();
                Files.writeString(
                        Path.of("transactions.log"),
                        line.toString() + System.lineSeparator(),
                        StandardOpenOption.APPEND
                );
            } catch (IOException e) {
                throw new RuntimeException("Impossible d'écrire la transaction dans le fichier.", e);
            }
        };
    }
}
