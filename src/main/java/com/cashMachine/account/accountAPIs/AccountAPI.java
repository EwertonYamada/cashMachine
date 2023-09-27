package com.cashMachine.account.accountAPIs;

import com.cashMachine.account.account.Account;
import com.cashMachine.account.accountServices.AccountService;
import com.cashMachine.account.dtos.AccountDto;
import com.cashMachine.account.enums.AccountType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/account")
public class AccountAPI {
    private final AccountService accountService;

    public AccountAPI(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/new/savings")
    public ResponseEntity<Account> createSavingsAccount(@RequestBody AccountDto accountDto) {
        return ResponseEntity.ok(this.accountService.createAccount(accountDto, AccountType.SAVINGS, false));
    }

    @PostMapping("/new/checking")
    public ResponseEntity<Account> createCheckingAccount(@RequestBody AccountDto accountDto) {
        return ResponseEntity.ok(this.accountService.createAccount(accountDto, AccountType.CHECKING, false));
    }

    @PostMapping("/new/both")
    public ResponseEntity<List<Account>> createBothAccounts(@RequestBody AccountDto accountDto) {
        return ResponseEntity.ok(this.accountService.createAccounts(accountDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(this.accountService.getAccountById(id));
    }

    @GetMapping("/all-accounts")
    public ResponseEntity<List<Account>> getAllAgencies() {
        return ResponseEntity.ok(this.accountService.getAllAccounts());
    }


}
