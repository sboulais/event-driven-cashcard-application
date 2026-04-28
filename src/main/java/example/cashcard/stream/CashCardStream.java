package example.cashcard.stream;

import example.cashcard.domain.Transaction;
import example.cashcard.service.DataSourceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Supplier;

/**
 * La classe CashCardStream est une classe de configuration Spring, notre application
 * Spring Boot l'invoquera automatiquement et la rendra disponible. Pendant l'exécution
 * de notre application Spring Boot, Spring Cloud Stream appellera automatiquement la
 * méthode Supplier selon une planification définie, par défaut toutes les secondes.
 *
 * Lorsque la méthode Supplier est appelée, elle se contente d'appeler la méthode getData()
 * de la source de données. Les données récupérées par la méthode Supplier seront
 * automatiquement transmises à une source externe, telle qu'un canal Kafka, pour que
 * d'autres abonnés puissent y accéder.
 *
 */
@Configuration
public class CashCardStream {

    /**
     * Publie une nouvelle transaction toutes les secondes dans la
     * messagerie (Kafka ou autre). La méthode retourne un Supplier
     * de type Transaction :
     */
    @Bean
    Supplier<Transaction> approvalRequest(DataSourceService ds) {
        return ds::getData;
    }
}
