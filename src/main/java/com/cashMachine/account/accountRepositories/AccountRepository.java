package com.cashMachine.account.accountRepositories;

import com.cashMachine.account.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    @Query(nativeQuery = true,
            value = "SELECT COUNT(*) > 0 " +
                    "FROM account a " +
                    "JOIN agency ag " +
                    "ON a.agency_id = ag.id " +
                    "WHERE ag.bank_id = ( " +
                    "   SELECT bank_id " +
                    "   FROM agency ag2 " +
                    "   WHERE ag2.id = :agencyId) " +
                    "AND (a.associate_id = :associateId " +
                    "OR a.account_number = :accountNumber) ")
    boolean countMemberAlreadyHasAccountInBankOrBankNumberExists(@Param("agencyId") Long agencyId, @Param("associateId") Long associateId, @Param("accountNumber") Long accountNumber);

    @Query(nativeQuery = true,
            value = "SELECT COUNT(*) > 0 " +
                    "FROM account a " +
                    "WHERE a.account_number = :accountNumber " +
                    "AND a.associate_id = :associateId " +
                    "AND a.agency_id = :agencyId")
    boolean checkThePersonCreatingAccount(@Param("agencyId") Long agencyId, @Param("associateId") Long associateId, @Param("accountNumber") Long accountNumber);

    @Query(nativeQuery = true,
            value = "SELECT COUNT(*) > 0 " +
                    "FROM account a " +
                    "WHERE a.account_number = :accountNumber " +
                    "AND a.account_type = :accountType")
    boolean checkIfAccountTypeAlreadyExists(@Param("accountNumber") Long accountNumber, @Param("accountType") String accountType);

    @Query(nativeQuery = true,
            value = "SELECT a.balance " +
                    "FROM account a " +
                    "WHERE a.id = :sourceAccountId ")
    BigDecimal getBalance(@Param("sourceAccountId") Long sourceAccountId);

    @Query(nativeQuery = true,
            value = "SELECT b.full_balance_transaction" +
                    "FROM bank b ")
    Long getBankIdByAccountId(Long sourceAccount);

//    @Query(nativeQuery = true,
//            value = )
}
