package com.cashMachine.account.accountServices;

import com.cashMachine.account.account.Account;
import com.cashMachine.account.accountRepositories.AccountRepository;
import com.cashMachine.account.dtos.AccountDto;
import com.cashMachine.agency.agencyServices.AgencyService;
import com.cashMachine.associate.associateServices.AssociateService;
import com.cashMachine.transaction.enums.AccountType;
import com.cashMachine.transaction.enums.TransactionType;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final AssociateService associateService;
    private final AgencyService agencyService;

    public AccountService(AccountRepository accountRepository, AssociateService associateService, AgencyService agencyService) {
        this.accountRepository = accountRepository;
        this.associateService = associateService;
        this.agencyService = agencyService;
    }

    public Account newAccount(AccountDto accountDto, AccountType accountType) {
        switch(accountType) {
            case CHECKING:
                break;
            case SAVING:
                break;
        }
        this.validateAccountCreation(accountDto.getAgency(), accountDto.getAssociate(), accountDto.getNumber(), accountType.toString());
        return createAccount(accountDto,accountType);
    }
    public Account createAccount(AccountDto accountDto, AccountType accountType) {
        Account account = new Account();
        account.setNumber(accountDto.getNumber());
        account.setAssociate(this.associateService.getAssociateById(accountDto.getAssociate()));
        account.setAgency(this.agencyService.getAgencyById(accountDto.getAgency()));
        account.setBalance(new BigDecimal(0));
        account.setType(accountType.toString());
        return this.accountRepository.save(account);
    }

    private void validateIfMemberAlreadyHasAccountInBank(Long agencyId, Long associateId) {
        if (this.accountRepository.countMemberAlreadyHasAccountInBankOrBankNumberExists(agencyId,associateId,null)) {
            throw new RuntimeException("Associado já possui conta no banco!");
        }
    }

    private void validateIfAccontNumberExists(Long agencyId, Long accountNumber) {
        if (this.accountRepository.countMemberAlreadyHasAccountInBankOrBankNumberExists(agencyId,null,accountNumber)) {
            throw new RuntimeException("Número de conta já utilizada!");
        }
    }

    private void validateAccountCreation(Long agencyId, Long associateId, Long accountNumber, String accountType) {
        if (!this.accountRepository.checkThePersonCreatingAccount(agencyId, associateId, accountNumber)) {
            validateIfAccontNumberExists(agencyId, accountNumber);
            validateIfMemberAlreadyHasAccountInBank(agencyId,associateId);
        }
        if (this.accountRepository.checkIfAccountTypeAlreadyExists(accountNumber,accountType,agencyId)) {
            throw new RuntimeException("Você já possui uma conta do tipo ".concat(accountType));
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
