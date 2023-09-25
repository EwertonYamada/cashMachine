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
            value = "   SELECT COUNT(*) > 0 " +
                    "   FROM account a " +
                    "   JOIN agency ag " +
                    "       ON a.agency_id = ag.id " +
                    "   WHERE a.account_number = :accountNumber " +
                    "       AND ag.bank_id = ( " +
                    "       SELECT bank_id " +
                    "       FROM agency ag2 " +
                    "       WHERE ag2.id = :agencyId) ")
    boolean countAccountNumberInBank(@Param("agencyId") Long agencyId, @Param("accountNumber") Long accountNumber);

    @Query(nativeQuery = true,
            value = "   SELECT COUNT(*) > 0 " +
                    "   FROM account a " +
                    "   WHERE a.agency_id = :agencyId " +
                    "       AND a.associate_id = :associateId")
    boolean countMemberAlreadyHasAccountInBank(Long associateId, Long agencyId);

    @Query(nativeQuery = true,
            value = "SELECT a.balance " +
                    "FROM account a " +
                    "WHERE a.id = :sourceAccountId ")
    BigDecimal getBalance(@Param("sourceAccountId") Long sourceAccountId);

    @Query(nativeQuery = true,
            value = "SELECT b.full_balance_transaction" +
                    "FROM bank b " /*+
                    "WHERE "*/)
    Long getBankIdByAccountId(Long sourceAccount);
}
