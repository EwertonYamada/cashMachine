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
                    "   WHERE ( a.associate_id = :associateId " +
                    "       AND a.account_type = :accountType ) ")
    boolean validateIfMemberAlreadyHasThisTypeOfAccountAtThatBank(@Param("associateId") Long associateId,
                                                                  @Param("accountType") String accountType);

    @Query(nativeQuery = true,
            value = "    SELECT COUNT(associate_id) > 0 " +
                    "    FROM account a " +
                    "    WHERE  ( a.account_number = :accountNumber " +
                    "       AND a.agency_id = :agencyId " +
                    "       AND a.associate_id != :associateId ) ")
    boolean validateIfTheAccountNumberIsAlreadyUsedByAMember(@Param("accountNumber") Long accountNumber,
                                                             @Param("agencyId") Long agencyId,
                                                             @Param("associateId") Long associateId);

    @Query(nativeQuery = true,
            value = "SELECT a.balance " +
                    "FROM account a " +
                    "WHERE a.id = :sourceAccountId ")
    BigDecimal getBalance(@Param("sourceAccountId") Long sourceAccountId);

}
