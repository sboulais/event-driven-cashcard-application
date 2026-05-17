package example.cashcard.service;

import example.cashcard.domain.ApprovalStatus;
import example.cashcard.domain.CardHolderData;
import example.cashcard.domain.EnrichedTransaction;
import example.cashcard.domain.Transaction;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class EnrichementService {

    private static final List<String> FRENCH_ADDRESSES = List.of(
            "1 rue de la Paix, Paris",
            "5 impasse des Mûriers, Lyon",
            "12 avenue des Champs-Élysées, Paris",
            "3 rue du Moulin, Bordeaux",
            "8 allée des Roses, Toulouse",
            "15 boulevard Haussmann, Paris",
            "22 rue Victor Hugo, Marseille",
            "7 chemin des Lilas, Nantes",
            "9 place de la République, Strasbourg",
            "4 rue des Fleurs, Nice",
            "18 avenue Jean Jaurès, Lille",
            "6 rue du Général de Gaulle, Rennes",
            "11 impasse de la Forêt, Grenoble",
            "2 rue des Acacias, Montpellier",
            "20 allée du Parc, Rouen",
            "14 rue Nationale, Tours",
            "30 boulevard de la Liberté, Nantes",
            "5 rue du Château, Dijon",
            "13 avenue de la Gare, Reims",
            "17 rue des Vignes, Bordeaux",
            "25 chemin du Moulin, Aix-en-Provence",
            "10 rue Saint-Michel, Toulouse",
            "33 boulevard Victor Hugo, Nice",
            "6 allée des Peupliers, Clermont-Ferrand",
            "19 rue de la Fontaine, Rouen",
            "8 place du Marché, Angers",
            "27 avenue de la République, Caen",
            "3 rue des Cerisiers, Metz",
            "16 chemin des Oliviers, Montpellier",
            "21 rue du Port, Brest",
            "4 allée des Marronniers, Nancy",
            "9 rue de l'Église, Orléans",
            "35 avenue Foch, Strasbourg",
            "7 rue des Pins, Toulon",
            "23 boulevard des Capucines, Paris",
            "11 chemin de la Colline, Besançon",
            "2 place de la Mairie, Perpignan",
            "28 rue des Tilleuls, Amiens",
            "5 allée des Saules, Le Mans",
            "14 avenue du Président Wilson, Limoges",
            "31 rue des Platanes, Nîmes",
            "6 chemin des Chênes, Poitiers",
            "18 boulevard des Alpes, Grenoble",
            "10 rue de la Mer, La Rochelle",
            "24 avenue des Pyrénées, Pau",
            "3 impasse du Verger, Valence",
            "12 rue des Hirondelles, Dunkerque",
            "26 allée des Mimosas, Antibes",
            "8 rue du Beffroi, Arras",
            "15 chemin des Lavandes, Avignon"
    );

    private static final Random RANDOM = new Random();

    public EnrichedTransaction enrichTransaction(Transaction transaction) {
        if (transaction.cashcard().amountRequestedForAuth() < 0)
            throw new IllegalArgumentException("Le montant demandé ne peut pas être négatif.");

        return new EnrichedTransaction(
                transaction.id(),
                transaction.cashcard(),
                ApprovalStatus.values()[RANDOM.nextInt(ApprovalStatus.values().length)],
                new CardHolderData(
                        UUID.randomUUID(),
                        transaction.cashcard().owner(),
                        FRENCH_ADDRESSES.get(RANDOM.nextInt(FRENCH_ADDRESSES.size())))
        );
    }
}
