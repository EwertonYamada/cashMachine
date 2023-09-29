package com.cashMachine.account.accountServices;

import com.cashMachine.account.account.Account;
import com.cashMachine.account.accountRepositories.AccountRepository;
import com.cashMachine.account.dtos.AccountDto;
import com.cashMachine.account.enums.AccountType;
import com.cashMachine.agency.agencyServices.AgencyService;
import com.cashMachine.associate.associateServices.AssociateService;
import org.springframework.stereotype.Service;

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

    public Account createAccount(AccountDto accountDto, AccountType accountType) {
        switch (accountType) {
            case SAVING:
                this.validateCreateAccount(accountDto, accountType);
                break;
            case CHECKING:
                this.validateCreateAccount(accountDto, accountType);
                break;
            case BOTH:
                this.validateBothTypesAccounts(accountDto);
                this.accountRepository.save(this.fittingDTOOnAccount(accountDto, AccountType.CHECKING));
                accountType = AccountType.SAVING;
                break;
        }
        return this.accountRepository.save(this.fittingDTOOnAccount(accountDto, accountType));
    }

    private void validateBothTypesAccounts(AccountDto accountDto) {
        this.validateIfAccontNumberExists(accountDto.getAgency(), accountDto.getNumber());
        try {
            this.validateIfMemberAlreadyHasAccountInBankWithType(accountDto.getAssociate(), accountDto.getAgency(), AccountType.CHECKING.toString());
            this.validateIfMemberAlreadyHasAccountInBankWithType(accountDto.getAssociate(), accountDto.getAgency(), AccountType.SAVING.toString());
        } catch (RuntimeException e) {
            throw new RuntimeException("Não é possivel criar ambas contas porque ".concat(e.getMessage()));
        }
    }

    private Account fittingDTOOnAccount(AccountDto accountDto, AccountType accountType) {
        Account account = new Account();
        account.setNumber(accountDto.getNumber());
        account.setAssociate(this.associateService.getAssociateById(accountDto.getAssociate()));
        account.setAgency(this.agencyService.getAgencyById(accountDto.getAgency()));
        account.setBalance(new BigDecimal(0));
        account.setTypeAccount(accountType.toString());
        return account;
    }

    private void validateCreateAccount(AccountDto accountDto, AccountType accountType) {
        this.validateIfAccontNumberExists(accountDto.getAgency(), accountDto.getNumber());
        this.validateIfMemberAlreadyHasAccountInBankWithType(accountDto.getAssociate(), accountDto.getAgency(), accountType.toString());
    }

    private void validateIfMemberAlreadyHasAccountInBankWithType(Long associateId, Long agencyId, String typeAccount) {
        List<Account> account = this.accountRepository.findAccountInBank(associateId, agencyId);
        if (!account.isEmpty()) {
            if (account.size() > 1) {
                throw new RuntimeException("Esse associado ja foi cadastrado nesse banco com ambos tipos de contas!");
            }
            if (account.get(0).getTypeAccount().equals(typeAccount)) {
                throw new RuntimeException("Associado já possui conta do tipo: ".concat(typeAccount).concat(" nesse banco!"));
            } else if (!account.get(0).getAgency().equals(this.agencyService.getAgencyById(agencyId))) {
                throw new RuntimeException("Associado já tem uma conta nesse banco, e para criar uma conta do tipo ".concat(typeAccount).concat(" é necessario estar na mesma agencia que a primeira conta"));
            }
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

    public Account findCheckingAccountByAssoiciateAndAgency(Account sourceAccount) {
        return this.accountRepository.findCheckingAccountInBankByAssociate(sourceAccount.getAssociate(), sourceAccount.getAgency(), AccountType.CHECKING.toString());
    }
}
