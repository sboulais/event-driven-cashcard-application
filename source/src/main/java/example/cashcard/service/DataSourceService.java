package example.cashcard.service;

import example.cashcard.domain.Cashcard;
import example.cashcard.domain.Transaction;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class DataSourceService {

    private static final List<String> FRENCH_FIRST_NAMES = List.of(
            "Alice", "Baptiste", "Camille", "David", "Emma",
            "François", "Gabrielle", "Hugo", "Inès", "Julien",
            "Karine", "Lucas", "Marie", "Nicolas", "Océane",
            "Pierre", "Quentin", "Romain", "Sophie", "Thomas",
            "Ugo", "Valentine", "William", "Xavier", "Yasmine",
            "Zoé", "Antoine", "Béatrice", "Cédric", "Delphine",
            "Étienne", "Flavie", "Gilles", "Hélène", "Ivan",
            "Justine", "Kevin", "Laure", "Mathieu", "Nathalie",
            "Olivier", "Pauline", "Raphaël", "Sandrine", "Théo",
            "Ursula", "Victor", "Wendy", "Axel", "Yann"
    );

    private static final Random RANDOM = new Random();

    // Cette méthode est utilisée pour générer un flux de transactions.
    public Transaction getData() {
        Cashcard cashcard = new Cashcard(
                Math.abs(new Random().nextLong(65535)),
                FRENCH_FIRST_NAMES.get(RANDOM.nextInt(FRENCH_FIRST_NAMES.size())),
                Math.abs(Math.round(new Random().nextDouble(100.0) * 100.0) / 100.0)
        );
        return new Transaction(Math.abs(new Random().nextLong(65535)), cashcard);
    }
}
