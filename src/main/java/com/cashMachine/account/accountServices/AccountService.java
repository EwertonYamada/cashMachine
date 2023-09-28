package com.cashMachine.account.accountServices;

import com.cashMachine.account.account.Account;
import com.cashMachine.account.accountRepositories.AccountRepository;
import com.cashMachine.account.dtos.AccountDto;
import com.cashMachine.agency.agencyServices.AgencyService;
import com.cashMachine.associate.associateServices.AssociateService;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

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
        this.validateIfAccountNumberExists(accountDto.getAgency(), accountDto.getTypeAccount(), accountDto.getNumber());
        this.validateIfMemberAlreadyHasAccountInBank(accountDto.getAssociate(), accountDto.getAgency(), accountDto.getTypeAccount());
        this.validationTypeAccount(accountDto.getTypeAccount().toUpperCase());

        if(accountDto.getTypeAccount().toUpperCase().contentEquals("CHECKING-SAVING")){ this.createAccountCheckingSaving(accountDto); }

        Account account = this.configNewAccount(accountDto, accountDto.getTypeAccount());
        return this.accountRepository.save(account);
    }

    private void validateIfMemberAlreadyHasAccountInBank(Long associateId, Long agencyId, String typeAccount) {
        if (this.accountRepository.countMemberAlreadyHasAccountInBank(associateId, agencyId, typeAccount.toUpperCase())) {
            throw new RuntimeException("Associado já possui esse tipo de conta no banco!");
        }
    }

    private void validateIfAccountNumberExists(Long agencyId, String typeAccount, Long accountNumber) {
        if (this.accountRepository.countAccountNumberInBank(agencyId,typeAccount.toUpperCase(), accountNumber)) {
            throw new RuntimeException("Número de conta já utilizada para esse tipo de conta!");
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

    public void validationTypeAccount(String typeAccount){
        if (Objects.isNull(typeAccount)){
            throw new RuntimeException("Por favor, informar o tipo da conta!");
        }
        if(!typeAccount.contentEquals("CHECKING")
                && !typeAccount.contentEquals("SAVING")
                && !typeAccount.contentEquals("CHECKING-SAVING")){
            throw new RuntimeException("Tipo de conta inválido!");
        }
    }

    public void createAccountCheckingSaving(AccountDto accountDto) {
        this.accountRepository.save(configNewAccount(accountDto, "CHECKING"));
        this.accountRepository.save(configNewAccount(accountDto, "SAVING"));
    }

    public Account configNewAccount(AccountDto accountDto, String typeAccount){
        Account account = new Account();
        account.setNumber(accountDto.getNumber());
        account.setAssociate(this.associateService.getAssociateById(accountDto.getAssociate()));
        account.setAgency(this.agencyService.getAgencyById(accountDto.getAgency()));
        account.setBalance(new BigDecimal(0));
        account.setTypeAccount(typeAccount.toUpperCase());
        return account;
    }

    public ResponseEntity<Object> createSecondAccount (Long firstAccountId){
        Account account = new Account();
        Account account1 = this.getAccountById(firstAccountId);

        if(this.accountRepository.countAccountsByNumberAnAndAgency(account1.getNumber(),account1.getAgency().getId()) > 1){
            throw new RuntimeException("Associado já possui tanto a conta corrente quando a poupança nesse banco!");
        }

        account.setNumber(account1.getNumber());
        account.setAgency(account1.getAgency());
        account.setAssociate(account1.getAssociate());
        account.setBalance(new BigDecimal(0));

        if(account1.getTypeAccount().contentEquals("CHECKING")){
            account.setTypeAccount("SAVING");
        } else if(account1.getTypeAccount().contentEquals("SAVING")) {
            account.setTypeAccount("CHECKING");
        }

        this.accountRepository.save(account);
        return ResponseEntity.ok("Sua conta ".concat(account.getTypeAccount()).concat(" foi criada!"));
    }

    public Account getCheckingAccountBySavingAccount(Long savingAccountId){
        Account checkingAccount = this.accountRepository.selectCheckingAccountBySavingAccount(savingAccountId);
        return checkingAccount;
    }

    //CONSERTAR A QUERY
//    public Long getBankIdByAccountId(Long sourceAccount) {
//        return this.accountRepository.getBankIdByAccountId(sourceAccount);
//    }
}
