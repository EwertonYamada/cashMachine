package com.cashMachine.bank.bankRepositories;

import com.cashMachine.bank.bank.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BankRepository extends JpaRepository<Bank, Long> {

    @Query(nativeQuery = true,
            value = "   SELECT COUNT(*) > 0 " +
                    "   FROM bank b " +
                    "   WHERE b.bank_number = :bankNumber " +
                    "   OR b.bank_name LIKE %:bankName% " )
    boolean countBankByNameOrNumber(@Param("bankName") String bankName, @Param("bankNumber") Long bankNumber);

    @Query(nativeQuery = true,
            value = "SELECT COUNT(*) > 0 " +
                    "FROM bank b " +
                    "JOIN agency a " +
                    "   ON a.bank_id = b.id" +
                    "JOIN account a2 " +
                    "   ON a2.agency_id = a.id " +
                    "WHERE b.id = :bankId")
    boolean checkForOpenAccountsByBankId(@Param("bankId") Long bankId);
}
