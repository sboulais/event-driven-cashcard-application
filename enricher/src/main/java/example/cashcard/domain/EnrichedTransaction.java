package example.cashcard.domain;

public record EnrichedTransaction(
        Long id,
        Cashcard cashcard,
        ApprovalStatus approvalStatus,
        CardHolderData cardHolderData
) {
}
