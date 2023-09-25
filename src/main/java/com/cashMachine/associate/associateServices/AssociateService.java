package com.cashMachine.associate.associateServices;

import com.cashMachine.associate.associate.Associate;
import com.cashMachine.associate.associateRepositories.AssociateRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.List;

@Service
public class AssociateService {
    private final AssociateRepository associateRepository;

    public AssociateService(AssociateRepository associateRepository) {
        this.associateRepository = associateRepository;
    }

    public Associate createAssociate(Associate associate) {
        this.validateIfAssociateExists(associate);
        return this.associateRepository.save(associate);
    }

    private void validateIfAssociateExists(Associate associate) {
        if (this.associateRepository.countAssociates(associate.getDocumentNumber())) {
        throw new RuntimeException("Associado com documento Nº ".concat(associate.getDocumentNumber()).concat(" já cadastrado"));
        }
    }

    public Associate getAssociateById(Long id) {
        return this.associateRepository.findById(id).orElseThrow(() -> {
            return new RuntimeException("Associado não encontrado!");
        });
    }

    public List<Associate> getAllAssociates(Pageable pageable) {
        return this.associateRepository.findAll((Sort) pageable);
    }
}
