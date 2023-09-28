package com.cashMachine.account.accountAPIs;

import com.cashMachine.account.account.Account;
import com.cashMachine.account.accountServices.AccountService;
import com.cashMachine.account.dtos.AccountDto;
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

    @PostMapping("/new")
    public ResponseEntity<Account> createAccount(@RequestBody AccountDto accountDto) {
        return ResponseEntity.ok(this.accountService.createAccount(accountDto));
    }

    @PostMapping("/second-account/{id}")
    public ResponseEntity<Object> secondAccount(@PathVariable("id") Long firstAccountId){
        return ResponseEntity.ok(this.accountService.createSecondAccount(firstAccountId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(this.accountService.getAccountById(id));
    }

    @GetMapping("/all-accounts")
    public ResponseEntity<List<Account>> getAllAgencies(@PageableDefault(page = 0, size = 10000, sort = "number",
            direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(this.accountService.getAllAccounts(pageable));
    }
}
