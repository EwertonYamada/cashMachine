package com.cashMachine.agency.agencyRepositories;

import com.cashMachine.agency.agency.Agency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AgencyRepository extends JpaRepository<Agency, Long> {
    @Query(nativeQuery = true,
            value = "   SELECT COUNT(*) > 0 " +
                    "   FROM agency a " +
                    "   WHERE a.bank_id = :bank " +
                    "   AND a.agency_number = :agencyNumber")
    boolean countAgencyExists(@Param("bank") Long bank, @Param("agencyNumber") Long agencyNumber);
}
