package com.cashMachine.transaction.transactionRepositories;

import com.cashMachine.transaction.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query(nativeQuery = true,
            value = "SELECT COUNT(*) > 0 " +
                    "FROM transactions t " +
                    "WHERE t.transaction_type = :transactionType " +
                    "   AND t.transaction_date > :transactionDate " +
                    "   AND t.source_account = :sourceAccountId")
    boolean countWithdrawOrTransferTransactions(@Param("transactionType") String transactionType,
                                                @Param("transactionDate") Date transactionDate,
                                                @Param("sourceAccountId") Long sourceAccountId);

    @Query(nativeQuery = true,
            value = "SELECT ")
    boolean validateDailyTransactionLimit(BigDecimal value, Long sourceAccount);

    @Query(nativeQuery = true,
            value = "SELECT COALESCE(SUM(t.transaction_value), 0) " +
                    "FROM transactions t " +
                    "WHERE t.source_account = :sourceAccountId " +
                    "   AND t.transaction_type IN('WITHDRAW', 'TRANSFER') " +
                    "   AND t.transaction_date > :currentDate ")
    BigDecimal getExitTransactions(@Param("sourceAccountId") Long sourceAccountId,
                                   @Param("currentDate") Date currentDate);

    @Query(nativeQuery = true,
            value = " SELECT b.full_balance_transaction " +
                    " FROM bank b " +
                    " JOIN agency a " +
                    "    ON a.bank_id = b.id  " +
                    " JOIN account a2 " +
                    "    ON a2.agency_id = a.id  " +
                    " WHERE a2.id = :account ")
    BigDecimal getFullBalanceTransactionByAccountId(@Param("account") Long account);
}
