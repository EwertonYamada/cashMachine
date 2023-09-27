package com.cashMachine.account.accountServices;

import com.cashMachine.account.account.Account;
import com.cashMachine.account.accountRepositories.AccountRepository;
import com.cashMachine.account.dtos.AccountDto;
import com.cashMachine.account.enums.AccountType;
import com.cashMachine.agency.agencyServices.AgencyService;
import com.cashMachine.associate.associateServices.AssociateService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AssociateService associateService;
    private final AgencyService agencyService;

    public AccountService(AccountRepository accountRepository, AssociateService associateService, AgencyService agencyService) {
        this.accountRepository = accountRepository;
        this.associateService  = associateService;
        this.agencyService     = agencyService;
    }

    public List<Account> createAccounts(AccountDto accountDto) {
        List<Account> Accounts = new ArrayList<>();
        Accounts.add(createAccount(accountDto, AccountType.SAVINGS, true));
        Accounts.add(createAccount(accountDto, AccountType.CHECKING, true));

        for (Account account : Accounts) {
            this.accountRepository.save(account);
        }

        return Accounts;
    }
    public Account createAccount(AccountDto accountDto, AccountType accountType, Boolean both) {
        this.countMemberAlreadyHasAccountInBankWithThisAccountType(accountDto.getNumber(), accountType.toString());
        this.validateIfThereIsAlreadyMemberWithThatAccountType(accountDto.getAssociate(), accountDto.getAssociate());

        Account account = new Account();
        account.setNumber(accountDto.getNumber());
        account.setAssociate(this.associateService.getAssociateById(accountDto.getAssociate()));
        account.setAgency(this.agencyService.getAgencyById(accountDto.getAgency()));
        account.setBalance(BigDecimal.ZERO);
        account.setAccountType(accountType.toString());

        return (both) ? account : this.accountRepository.save(account);

    }

    private void countMemberAlreadyHasAccountInBankWithThisAccountType(Long associateId, String accountType) {
        if (this.accountRepository.countMemberAlreadyHasAccountInBankWithThisAccountType(associateId, accountType)) {
            throw new RuntimeException("Associado já possui conta no banco!");
        }
    }

    private void validateIfThereIsAlreadyMemberWithThatAccountType(Long accountNumber, Long associateId) {

        if (this.accountRepository.validateIfTheAccountNumberIsAlreadyUsedByAMember(accountNumber, associateId)) {
            throw new RuntimeException("Número de conta ".concat(accountNumber.toString())
                    .concat(" já utilizada ").concat(" em uma conta do tipo ").concat(associateId.toString()));
        }
    }

    public Account getAccountById(Long id) {
        try {
            return this.accountRepository.findById(id).get();
        } catch (Exception e) {
            throw new RuntimeException("Conta ID: ".concat(id.toString()).concat(" não encontrada!"));
        }
    }

    public List<Account> getAllAccounts() {
        return this.accountRepository.findAll();
    }

    public void updateBalance(Long accountId, BigDecimal value) {
        Account account = this.getAccountById(accountId);
        account.setBalance(account.getBalance().add(value));
        this.accountRepository.save(account);
    }

    public BigDecimal getBalance(Long accountId) {
        return this.accountRepository.getBalance(accountId);
    }

    public Long getBankIdByAccountId(Long sourceAccount) {
        return this.accountRepository.getBankIdByAccountId(sourceAccount);
    }
}
