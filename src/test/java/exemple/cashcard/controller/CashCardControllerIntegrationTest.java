package exemple.cashcard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.cashcard.CashcardApplication;
import example.cashcard.domain.Cashcard;
import example.cashcard.domain.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.test.web.servlet.client.ExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.test.web.servlet.client.assertj.RestTestClientResponse;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = CashcardApplication.class)
@Import({TestChannelBinderConfiguration.class})
class CashCardControllerIntegrationTest {

    private RestTestClient client;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        // Utilise le contexte Spring réel pour que les injections du contrôleur soient actives.
        client = RestTestClient.bindToApplicationContext(webApplicationContext)
                .build();
    }

    @Test
    void should_ReturnOk_When_PublishingTransaction() {

        // given
        Transaction transaction = new Transaction(1L,
                new Cashcard(1L, "Sébastien", 156.0)
        );

        // when
        ExchangeResult result = client.post()
                .uri("/publish/txn")
                .body(transaction)
                .exchange()
                .returnResult();

        // then
        assertThat(RestTestClientResponse.from(result)).hasStatusOk();
    }

    @Test
    void should_ResultNotNull_When_PublishingTransaction
            (@Autowired OutputDestination outputDestination) {

        // given
        Transaction transaction = new Transaction(1L,
                new Cashcard(1L, "Sébastien", 156.0)
        );

        client.post()
                .uri("/publish/txn")
                .body(transaction)
                .exchange()
                .returnResult();

        // when
        Message<byte[]> result = outputDestination.receive(5000, "approvalRequest-out-0");

        // then
        assertThat(result).isNotNull();
    }

    @Test
    void should_Receive_When_PublishingTransaction
            (@Autowired OutputDestination outputDestination) throws IOException {

        // given
        Transaction transaction = new Transaction(1L,
                new Cashcard(1L, "Sébastien", 156.0)
        );

        client.post()
                .uri("/publish/txn")
                .body(transaction)
                .exchange()
                .returnResult();

        // when
        Message<byte[]> result = outputDestination.receive(5000, "approvalRequest-out-0");

        ObjectMapper objectMapper = new ObjectMapper();
        Transaction actual = objectMapper.readValue(result.getPayload(), Transaction.class);

        // then
        assertThat(actual.id()).isEqualTo(transaction.id());
    }
}