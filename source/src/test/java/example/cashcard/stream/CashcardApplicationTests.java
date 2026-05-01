package example.cashcard.stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.cashcard.domain.Cashcard;
import example.cashcard.domain.Transaction;
import example.cashcard.service.DataSourceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
class CashcardApplicationTests {

	// Crée un mock de DataSourceService et l'enregistre comme bean Spring
	// dans l'ApplicationContext, remplaçant tout bean existant
	// du même type pendant les tests.
	@MockitoBean
	private DataSourceService dataSourceService;

	@Test
	void should_publishMessageToApprovalChannel_when_dataSourceReturnsValidTransaction
			(@Autowired OutputDestination outputDestination) {

		// given
		given(dataSourceService.getData()).willReturn(
				new Transaction(Long.valueOf(1),
						new Cashcard(Long.valueOf(1), "Sébastien", 156.0)
				)
		);

		// when
		Message<byte[]> actual = outputDestination.receive(5000, "approvalRequest-out-0");

		// then
		assertThat(actual).isNotNull();
	}

	@Test
	void should_publishMessageWithCorrectPayload_when_dataSourceReturnsValidTransaction
			(@Autowired OutputDestination outputDestination) throws IOException {

		// given
		Transaction expected = new Transaction(Long.valueOf(1),
				new Cashcard(Long.valueOf(1), "Sébastien", 156.0)
		);

		given(dataSourceService.getData()).willReturn(expected);

		// when
		Message<byte[]> message = outputDestination.receive(5000, "approvalRequest-out-0");
		ObjectMapper objectMapper = new ObjectMapper();
		Transaction actual = objectMapper.readValue(message.getPayload(), Transaction.class);

		// then
		assertEquals(expected, actual);
	}
}
