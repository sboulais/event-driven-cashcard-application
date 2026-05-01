package example.cashcard.service;

import example.cashcard.domain.Cashcard;
import example.cashcard.domain.Transaction;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class DataSourceService {

    // Cette méthode est utilisée pour générer un flux de transactions.
    public Transaction getData() {
        Cashcard cashcard = new Cashcard(
                Math.abs(new Random().nextLong(65535)),
                "Sébastien",
                Math.abs(Math.round(new Random().nextDouble(100.0) * 100.0) / 100.0)
        );
        return new Transaction(Math.abs(new Random().nextLong(65535)), cashcard);
    }
}
