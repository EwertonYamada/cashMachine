package com.cashMachine.account.accountAPIs;

import com.cashMachine.account.account.Account;
import com.cashMachine.account.accountServices.AccountService;
import com.cashMachine.account.dtos.AccountDto;
import com.cashMachine.account.enums.AccountType;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;
import java.util.List;

@RestController
@RequestMapping("/account")
public class AccountAPI {
    private final AccountService accountService;

    public AccountAPI(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/new-saving")
    public ResponseEntity<Account> createAccountSaving(@RequestBody AccountDto accountDto) {
        return ResponseEntity.ok(this.accountService.createAccount(accountDto, AccountType.SAVING));
    }

    @PostMapping("/new-checking")
    public ResponseEntity<Account> createAccountChecking(@RequestBody AccountDto accountDto) {
        return ResponseEntity.ok(this.accountService.createAccount(accountDto, AccountType.CHECKING));
    }

    @PostMapping("/new-both")
    public ResponseEntity<Account> createAccountBoth(@RequestBody AccountDto accountDto) {
        return ResponseEntity.ok(this.accountService.createAccount(accountDto, AccountType.BOTH));
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
