package com.cashMachine.account.accountRepositories;

import com.cashMachine.account.account.Account;
import com.cashMachine.agency.agency.Agency;
import com.cashMachine.associate.associate.Associate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    @Query(nativeQuery = true,
            value = "   SELECT COUNT(*) > 0 " +
                    "   FROM account a " +
                    "   JOIN agency ag " +
                    "       ON a.agency_id = ag.id " +
                    "   WHERE a.account_number = :accountNumber " +
                    "       AND ag.bank_id = ( " +
                    "       SELECT bank_id " +
                    "       FROM agency ag2 " +
                    "       WHERE ag2.id = :agencyId) ")
    boolean countAccountNumberInBank(@Param("agencyId") Long agencyId,
                                             @Param("accountNumber") Long accountNumber);

    @Query(nativeQuery = true,
            value = "   SELECT * " +
                    "   FROM account a " +
                    "   JOIN agency a2 " +
                    "   ON a2.id = a.agency_id " +
                    "   WHERE a.associate_id = :associateId " +
                    "   AND a2.bank_id = ( " +
                    "       SELECT bank_id " +
                    "       FROM agency a3 " +
                    "       WHERE a3.id = :agencyId )")
    List<Account> findAccountInBank(@Param("associateId") Long associateId,
                                    @Param("agencyId") Long agencyId);

    @Query(nativeQuery = true,
            value = "SELECT a.balance " +
                    "FROM account a " +
                    "WHERE a.id = :sourceAccountId ")
    BigDecimal getBalance(@Param("sourceAccountId") Long sourceAccountId);

    @Query(nativeQuery = true,
            value = "SELECT b.full_balance_transaction " +
                    "FROM bank b ")
    Long getBankIdByAccountId(Long sourceAccount);

    @Query(nativeQuery = true,
            value = "SELECT * " +
                    "FROM account a " +
                    "WHERE a.associate_id = :associate " +
                    "AND a.agency_id = :agency " +
                    "AND a.type_account = :accountType ")
    Account findCheckingAccountInBankByAssociate(@Param("associate") Associate associate,
                                                 @Param("agency") Agency agency,
                                                 @Param("accountType") String string);
}