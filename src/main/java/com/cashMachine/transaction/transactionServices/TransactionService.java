package com.cashMachine.transaction.transactionServices;

import com.cashMachine.account.account.Account;
import com.cashMachine.account.accountRepositories.AccountRepository;
import com.cashMachine.account.accountServices.AccountService;
import com.cashMachine.account.enums.AccountType;
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
            case RESCUE:
                this.validateBalanceAvaliability(transactionDto.getSourceAccount(), transactionDto.getValue());
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
        transaction.setTargetAccount(this.lookForTargetAccount(transactionDto, transactionType));
        transaction.setSourceAccount(this.lookForSourceAccount(transactionDto, transactionType));
        transaction.setTransactionType(transactionType.toString());
        if (transactionType.equals(TransactionType.TRANSFER)) {
            this.lookForSavingAccount(transaction);
        }
        this.transactionRepository.save(transaction);
        return transaction;
    }

    private void lookForSavingAccount(Transaction transaction) {
        if (transaction.getSourceAccount().getTypeAccount().equals(AccountType.SAVING.toString()) && transaction.getTransactionType().equals(TransactionType.TRANSFER.toString())) {
            throw new RuntimeException("Não é possivel fazer uma transferencia de uma conta do tipo poupança!");
        }
    }

    private Account lookForSourceAccount(TransactionDto transactionDto, TransactionType transactionType) {
        Account sourceAccount = null;
        if (Objects.nonNull(transactionDto.getSourceAccount()) && !transactionType.equals(TransactionType.DEPOSIT)) {
            sourceAccount = this.accountService.getAccountById(transactionDto.getSourceAccount());
            if (transactionType.equals(TransactionType.RESCUE) && sourceAccount.getTypeAccount().equals(AccountType.CHECKING.toString())) {
                throw new RuntimeException("Para fazer um Resgate a conta Origem deve ser do tipo Poupança!");
            }
            this.accountService.updateBalance(sourceAccount.getId(), transactionDto.getValue().multiply(new BigDecimal(-1)));
        }
        return sourceAccount;
    }

    private Account lookForTargetAccount(TransactionDto transactionDto, TransactionType transactionType) {
        Account targetAccount = null;
        if (Objects.nonNull(transactionDto.getTargetAccount())) {
            if (transactionType.equals(TransactionType.DEPOSIT) || transactionType.equals(TransactionType.TRANSFER)) {
                targetAccount = this.accountService.getAccountById(transactionDto.getTargetAccount());
                this.accountService.updateBalance(targetAccount.getId(), transactionDto.getValue());
            }
            if (transactionType.equals(TransactionType.RESCUE)) {
                targetAccount = this.validateTargetAccountForRescue(transactionDto.getSourceAccount());
                this.accountService.updateBalance(targetAccount.getId(), transactionDto.getValue());
            }
        }
        return targetAccount;
    }

    private Account validateTargetAccountForRescue(Long sourceAccountId) {
        Account sourceAccount = this.accountService.getAccountById(sourceAccountId);
        Account targetAccount = this.accountService.findCheckingAccountByAssoiciateAndAgency(sourceAccount);
        if (Objects.isNull(targetAccount)) {
            throw new RuntimeException("Não foi possível achar uma conta destino para enviar o dinheiro do resgate!");
        }
        return targetAccount;
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

    public List<Transaction> getAllTransctions(Pageable pageable) {
        return this.transactionRepository.findAll((Sort) pageable);
    }
}
