package com.cashMachine.associate.associateRepositories;

import com.cashMachine.associate.associate.Associate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AssociateRepository extends JpaRepository<Associate, Long> {
    @Query(nativeQuery = true,
            value = "   SELECT COUNT(*) > 0 " +
                    "   FROM associate a " +
                    "   WHERE a.document_number = :documentNumber ")
    boolean countAssociates(@Param("documentNumber") String documentNumber);
}
