package com.cashMachine.transaction.transactionServices;

import com.cashMachine.account.account.Account;
import com.cashMachine.account.accountRepositories.AccountRepository;
import com.cashMachine.account.accountServices.AccountService;
import com.cashMachine.bank.bankRepositories.BankRepository;
import com.cashMachine.transaction.TransactionDto;
import com.cashMachine.transaction.enums.TransactionType;
import com.cashMachine.transaction.transaction.Transaction;
import com.cashMachine.transaction.transactionRepositories.TransactionRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class TransactionService {
    private final AccountService accountService;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final BankRepository bankRepository;
    private BigDecimal TRANSACTIONS_PERCENTAGE_LIMIT = new BigDecimal(0.3);

    public TransactionService(AccountService accountService, TransactionRepository transactionRepository, AccountRepository accountRepository, BankRepository bankRepository) {
        this.accountService = accountService;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.bankRepository = bankRepository;
    }

    public Transaction newTransction(TransactionDto transactionDto, TransactionType transactionType) {
        switch (transactionType) {
            case DEPOSIT:
                break;
            case TRANSFER:
                this.generalValidations(transactionDto, transactionType);
                break;
            case WITHDRAW:
                this.generalValidations(transactionDto, transactionType);
                break;
        }
        return this.executeTransaction(transactionType,transactionDto);
    }

    private BigDecimal getOpeningBalanceOfTheDay(TransactionDto transactionDto) {
        BigDecimal openingBalanceOfTheDay = this.transactionRepository.getExitTransactions(transactionDto.getSourceAccount(),
                this.getCurrentDate()).add(this.accountService.getBalance(transactionDto.getSourceAccount()));
        return openingBalanceOfTheDay;
    }

    private Date getCurrentDate() {
        Calendar currentDate = Calendar.getInstance();
        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);
        currentDate.set(Calendar.MILLISECOND, 0);
        return currentDate.getTime();
    }


    private void generalValidations(TransactionDto transactionDto, TransactionType transactionType) {
        this.validateQuantityTransacions(transactionType, transactionDto.getSourceAccount());
        this.validateValueForTransaction(transactionDto);
        this.validateBalanceAvaliability(transactionDto.getSourceAccount(), transactionDto.getValue());
    }


    private void validateValueForTransaction(TransactionDto transactionDto) {
        BigDecimal allowedDailyValue = this.getOpeningBalanceOfTheDay(transactionDto).multiply(TRANSACTIONS_PERCENTAGE_LIMIT);
        BigDecimal fullLimitTransactionOfBank = this.transactionRepository.getFullBalanceTransactionByAccountId(transactionDto.getSourceAccount());
        BigDecimal valueMovedOnTheDayPlusValueToBeMoved = this.transactionRepository.getExitTransactions(transactionDto.getSourceAccount(),
                getCurrentDate()).add(transactionDto.getValue());

        if (getOpeningBalanceOfTheDay(transactionDto).compareTo(fullLimitTransactionOfBank) > 0 &&
                allowedDailyValue.compareTo(fullLimitTransactionOfBank) < 1 ) {
            if (valueMovedOnTheDayPlusValueToBeMoved.compareTo(fullLimitTransactionOfBank) > 0) {
                throw new RuntimeException("Valor das transações diárias excede o permitido");
            }
        }
        if (getOpeningBalanceOfTheDay(transactionDto).compareTo(fullLimitTransactionOfBank) > 0 &&
                allowedDailyValue.compareTo(fullLimitTransactionOfBank) > 0 ) {
            if (valueMovedOnTheDayPlusValueToBeMoved.compareTo(allowedDailyValue) > 0) {
                throw new RuntimeException("Valor das transações diárias excede o permitido");
            }
        }
    }

    private Transaction executeTransaction(TransactionType transactionType, TransactionDto transactionDto) {
        Transaction transaction = new Transaction();
        transaction.setDate(new Date());
        transaction.setValue(new BigDecimal(String.valueOf(transactionDto.getValue())).setScale(2, RoundingMode.HALF_EVEN));
        if (Objects.nonNull(transactionDto.getTargetAccount()) && (transactionType.equals(TransactionType.DEPOSIT) ||
                transactionType.equals(TransactionType.TRANSFER))) {
            Account targetAccount = this.accountService.getAccountById(transactionDto.getTargetAccount());
            transaction.setTargetAccount(targetAccount);
            this.accountService.updateBalance(targetAccount.getId(), transactionDto.getValue());
        }
        if (Objects.nonNull(transactionDto.getSourceAccount()) && (transactionType.equals(TransactionType.WITHDRAW) || transactionType.equals(TransactionType.TRANSFER))) {
            Account sourceAccount = this.accountService.getAccountById(transactionDto.getSourceAccount());
            transaction.setSourceAccount(this.accountService.getAccountById(transactionDto.getSourceAccount()));
            this.accountService.updateBalance(sourceAccount.getId(), transactionDto.getValue().multiply(new BigDecimal(-1)));
        }
        transaction.setTransactionType(transactionType.toString());
        this.transactionRepository.save(transaction);
        return transaction;
    }

    public Transaction getTransctionById(Long id) {
        return this.transactionRepository.findById(id).orElseThrow(() -> {
            return new RuntimeException("Transação não encontrada!");
        });
    }

    private void validateQuantityTransacions(TransactionType transactionType, Long sourceAccountId) {
        if (this.transactionRepository.countWithdrawOrTransferTransactions(transactionType.toString(),
                this.getCurrentDate(),sourceAccountId)) {
            throw new RuntimeException("Transação do tipo ".concat(transactionType.toString()).concat(
                                   " já realizada no dia de hoje!"));
        }
    }

    private void validateBalanceAvaliability(Long sourceAccountId, BigDecimal transactionValue) {
        if (this.accountService.getBalance(sourceAccountId).compareTo(transactionValue) < 0) {
            throw new RuntimeException("Saldo em conta insuficiente para realizar transação!");
        }
    }

    public List<Transaction> getAllTransctions() {
        return this.transactionRepository.findAll();
    }
}
