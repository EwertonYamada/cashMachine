package com.cashMachine.bank.bankAPIs;

import com.cashMachine.bank.bank.Bank;
import com.cashMachine.bank.bankServices.BankService;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;
import java.util.List;

@RestController
@RequestMapping("/bank")
public class BankAPI {

    final BankService bankService;

    public BankAPI(BankService bankService) {
        this.bankService = bankService;
    }

    @PostMapping("/new")
    public ResponseEntity<Bank> createBank(@RequestBody Bank bank) {
        return ResponseEntity.ok(this.bankService.createNewBank(bank));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bank> getBankById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(this.bankService.getBankById(id));
    }

    @GetMapping("/all-banks")
    public ResponseEntity<List<Bank>> getAllBanks() {
        return ResponseEntity.ok(this.bankService.getAllBanks());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Bank> replaceBank(@RequestBody Bank bank, @PathVariable Long id) {
        return ResponseEntity.ok(this.bankService.replaceBank(bank, id));
    }

    @DeleteMapping("/delete/{id}")
    public void deleteBank(@PathVariable Long id) {
        this.bankService.deleteBank(id);
    }
}
