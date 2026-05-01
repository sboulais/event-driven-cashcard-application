package example.cashcard.controller;

import example.cashcard.domain.Transaction;
import example.cashcard.ondemand.CashcardTransactionOnDemand;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CashCardController {

    private CashcardTransactionOnDemand cashcardTransactionOnDemand;

    public CashCardController(CashcardTransactionOnDemand cashcardTransactionOnDemand) {
        this.cashcardTransactionOnDemand = cashcardTransactionOnDemand;
    }

    @PostMapping("/publish/txn")
    public void publishTxn(@RequestBody Transaction transaction) {
        System.out.println("Publishing transaction: " + transaction);
        cashcardTransactionOnDemand.publishTransaction(transaction);
    }
}
