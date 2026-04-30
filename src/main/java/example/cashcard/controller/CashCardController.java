package example.cashcard.controller;

import example.cashcard.domain.Transaction;
import example.cashcard.stream.CashCardStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CashCardController {

    private CashCardStream cashCardStream;

    public CashCardController(CashCardStream cashCardStream) {
        this.cashCardStream = cashCardStream;
    }

    @PostMapping("/publish/txn")
    public void publishTxn(@RequestBody Transaction transaction) {
        System.out.println("Publishing transaction: " + transaction);
        cashCardStream.publishTransaction(transaction);
    }
}
