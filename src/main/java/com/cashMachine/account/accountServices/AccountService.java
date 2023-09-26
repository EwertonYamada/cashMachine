package com.cashMachine.account.accountServices;

import com.cashMachine.account.account.Account;
import com.cashMachine.account.accountRepositories.AccountRepository;
import com.cashMachine.account.dtos.AccountDto;
import com.cashMachine.account.enums.AccountType;
import com.cashMachine.agency.agencyServices.AgencyService;
import com.cashMachine.associate.associateServices.AssociateService;
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

    public Account createAccount(AccountDto accountDto) {
        this.validateIfAccontNumberExists(accountDto.getAgency(), accountDto.getNumber());
        this.validateIfMemberAlreadyHasAccountInBank(accountDto.getAssociate(), accountDto.getAgency());
        // validar se o número da conta já existe para algum associado que tenha aquele tipo de conta;

        String saveAccountType =  AccountType.SAVINGS.toString();
        boolean createTwoAccounts = accountDto.getAccountType().equals(2);

        if(accountDto.getAccountType().equals(0) || createTwoAccounts) {
            registerAccount(accountDto, saveAccountType);
        }
        if(accountDto.getAccountType().equals(1) || createTwoAccounts) {
            saveAccountType =  AccountType.CHECKING.toString();
            registerAccount(accountDto, saveAccountType);
        }

    }
    public Account registerAccount(AccountDto accountDto, String accountType) {
        Account account = new Account();
        account.setNumber(accountDto.getNumber());
        account.setAssociate(this.associateService.getAssociateById(accountDto.getAssociate()));
        account.setAgency(this.agencyService.getAgencyById(accountDto.getAgency()));
        account.setBalance(BigDecimal.ZERO);
        account.setAccountType(accountType);
        return this.accountRepository.save(account);
    }

    private void validateIfMemberAlreadyHasAccountInBank(Long associateId, Long agencyId) {
        if (this.accountRepository.countMemberAlreadyHasAccountInBank(associateId, agencyId)) {
            throw new RuntimeException("Associado já possui conta no banco!");
        }
    }

    private void validateIfAccontNumberExists(Long agencyId, Long accountNumber) {
        if (this.accountRepository.countAccountNumberInBank(agencyId, accountNumber)) {
            throw new RuntimeException("Número de conta já utilizada!");
        }
    }

    public Account getAccountById(Long id) {
        try {
            return this.accountRepository.findById(id).get();
        } catch (Exception e) {
            throw new RuntimeException("Conta ID: ".concat(id.toString()).concat(" não encontrada!"));
        }
    }

    public List<Account> getAllAccounts(Pageable pageable) {
        return this.accountRepository.findAll((Sort) pageable);
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
