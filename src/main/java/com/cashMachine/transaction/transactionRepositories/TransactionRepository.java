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
            value = "SELECT a.associate_id " +
                    "FROM account a " +
                    "JOIN transactions  t ON a.id = t.source_account " +
                    "WHERE t.source_account = :sourceAccount ")
    Long getAssociateIdBySourceAccount(@Param("sourceAccount") Long sourceAccount);

    @Query(nativeQuery = true,
            value = "SELECT a.agency_id " +
                    "FROM account a " +
                    "JOIN transactions  t ON a.id = t.source_account " +
                    "WHERE t.source_account = :sourceAccount ")
    Long getAgencyIdBySourceAccount(@Param("sourceAccount") Long sourceAccount);

    @Query(nativeQuery = true,
            value = "SELECT COALESCE(SUM(t.transaction_value), 0) " +
                    "FROM transactions t " +
                    "JOIN account a " +
                    "ON t.source_account = a.id " +
                    "WHERE  t.transaction_date > :currentDate " +
                    "AND  t.source_account = :sourceAccountId " +
                    "AND a.account_type = :accountType " +
                    "AND (t.transaction_type = (SELECT " +
                    "   CASE " +
                    "       WHEN a.account_type = 'SAVING' THEN 'WITHDRAW' " +
                    "       WHEN a.account_type = 'CHECKING' THEN  'WITHDRAW' " +
                    "   END) " +
                    "OR t.transaction_type = (SELECT " +
                    "   CASE " +
                    "       WHEN a.account_type = 'SAVING' THEN 'RESCUE' " +
                    "       WHEN a.account_type = 'CHECKING' THEN 'TRANSFER' " +
                    "   END)) ")
    BigDecimal getExitTransactions(@Param("sourceAccountId") Long sourceAccountId, @Param("currentDate") Date currentDate, @Param("accountType") String accountType);

    @Query(nativeQuery = true,
            value = "SELECT b.full_balance_transaction " +
                    "FROM bank b " +
                    "JOIN agency a " +
                    "   ON a.bank_id = b.id  " +
                    "JOIN account a2 " +
                    "   ON a2.agency_id = a.id  " +
                    "WHERE a2.id = :account ")
    BigDecimal getFullBalanceTransactionByAccountId(@Param("account") Long account);

    @Query(nativeQuery = true,
            value = "SELECT account_type " +
                    "FROM account a " +
                    "WHERE a.id = :accountId ")
    String checkAccountType(@Param("accountId") Long accountId);

    @Query(nativeQuery = true,
            value = "SELECT id " +
                    "FROM account a " +
                    "WHERE a.associate_id  = :associateId " +
                    "AND a.agency_id = :agencyId " +
                    "AND account_type = 'CHECKING'")
    Long getCheckingAccountIdByAssociateAndAgencyId(@Param("associateId") Long associateId, @Param("agencyId") Long agencyId);
}
