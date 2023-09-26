package com.cashMachine.bank.bankServices;

import com.cashMachine.bank.bank.Bank;
import com.cashMachine.bank.bankRepositories.BankRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
import java.math.BigDecimal;
import java.util.List;

@Service
public class BankService {

    private final BankRepository bankRepository;

    public BankService(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    @Transactional
    public Bank createNewBank(Bank bank) {
        this.validateIfBankExists(bank.getBankName(), bank.getBankNumber());
        return this.bankRepository.save(bank);
    }

    private void validateIfBankExists(String bankName, Long bankNumber) {
        if (this.bankRepository.countBankByNameOrNumber(bankName, bankNumber)) {
            throw new RuntimeException("Banco ou número de banco já cadastrado!");
        }
    }

    @Transactional(readOnly = true)
    public Bank getBankById(Long id) {
        return this.findBankById(id);
    }

    @Transactional(readOnly = true)
    public List<Bank> getAllBanks() {
        return this.bankRepository.findAll();
    }

    public Bank replaceBank(Bank bank, Long id) {
        Bank bankToBeUpdate = this.findBankById(id);
        bankToBeUpdate.setBankName(bank.getBankName());
        bankToBeUpdate.setBankNumber(bank.getBankNumber());
        bankToBeUpdate.setFullBalanceTransaction(bank.getFullBalanceTransaction());
        return this.bankRepository.save(bankToBeUpdate);
    }

    public Bank findBankById(Long id) {
        try {
            return this.bankRepository.findById(id).get();
        } catch (Exception e) {
            throw new RuntimeException("Banco não encontrado.");
        }
    }

    public void deleteBank(Long id) {
        this.findBankById(id);
        this.checkForOpenAccountsByBankId(id);
        this.bankRepository.deleteById(id);
    }

    private void checkForOpenAccountsByBankId(Long bankId) {
        if(this.bankRepository.checkForOpenAccountsByBankId(bankId)) {
            throw new RuntimeException("Não é possível cadastrar um banco pois existe alguma conta ligada a ele.");
        }
    }
}
