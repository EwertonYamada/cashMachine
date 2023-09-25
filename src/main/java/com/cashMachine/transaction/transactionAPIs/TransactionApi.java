package com.cashMachine.transaction.transactionAPIs;


import com.cashMachine.transaction.TransactionDto;
import com.cashMachine.transaction.enums.TransactionType;
import com.cashMachine.transaction.transaction.Transaction;
import com.cashMachine.transaction.transactionServices.TransactionService;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;
import java.util.List;
@RestController
@RequestMapping("/transaction")
public class TransactionApi {
    private final TransactionService transactionService;

    public TransactionApi(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<Transaction> executeDeposit(@RequestBody TransactionDto transactionDto) {
        return ResponseEntity.ok(this.transactionService.newTransction(transactionDto, TransactionType.DEPOSIT));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Transaction> executeWithdraw(@RequestBody TransactionDto transactionDto) {
        return ResponseEntity.ok(this.transactionService.newTransction(transactionDto, TransactionType.WITHDRAW));
    }

    @PostMapping("/transfer")
    public ResponseEntity<Transaction> executeTransfer(@RequestBody TransactionDto transactionDto) {
        return ResponseEntity.ok(this.transactionService.newTransction(transactionDto, TransactionType.TRANSFER));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransctionById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(this.transactionService.getTransctionById(id));
    }

    @GetMapping("/all-transactions")
    public ResponseEntity<List<Transaction>> getAllTransctions(@PageableDefault(page = 0, size = 10000, sort = "number",
            direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(this.transactionService.getAllTransctions(pageable));
    }

}
