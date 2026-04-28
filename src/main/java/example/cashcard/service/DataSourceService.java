package example.cashcard.service;

import example.cashcard.domain.Cashcard;
import example.cashcard.domain.Transaction;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class DataSourceService {

    public Transaction getData() {
        Cashcard cashcard = new Cashcard(
                new Random().nextLong(),
                "Sébastien",
                new Random().nextDouble(100.0)

        );
        return new Transaction(new Random().nextLong(), cashcard);
    }
}
