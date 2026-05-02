package example.cashcard.domain;

public record Cashcard(
        Long id,
        String owner,
        Double amountRequestedForAuth) {
}

