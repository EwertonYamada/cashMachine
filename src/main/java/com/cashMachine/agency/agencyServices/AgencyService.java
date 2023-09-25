package com.cashMachine.agency.agencyServices;

import com.cashMachine.agency.agency.Agency;
import com.cashMachine.agency.agencyRepositories.AgencyRepository;
import com.cashMachine.agency.dtos.AgencyDto;
import com.cashMachine.bank.bank.Bank;
import com.cashMachine.bank.bankServices.BankService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.List;

@Service
public class AgencyService {
    private final AgencyRepository agencyRepository;
    private final BankService bankService;
    public AgencyService(AgencyRepository agencyRepository, BankService bankService) {
        this.agencyRepository = agencyRepository;
        this.bankService = bankService;
    }

    public Agency createAgency(AgencyDto agencyDto) {
        Bank bank = this.bankService.findBankById(agencyDto.getBankId());
        this.validateIfAgencyExists(agencyDto.getBankId(), agencyDto.getNumber());
        Agency agency = new Agency();
        agency.setName(agencyDto.getName());
        agency.setNumber(agencyDto.getNumber());
        agency.setBank(bank);
        return this.agencyRepository.save(agency);
    }

    private void validateIfAgencyExists(Long bankId, Long agencyNumber) {
        if (this.agencyRepository.countAgencyExists(bankId, agencyNumber)) {
            throw new RuntimeException("O banco ID ".concat(bankId.toString()).
                    concat(" já possui uma agência com o nº ").concat(agencyNumber.toString()));
        }
    }

    public Agency getAgencyById(Long id) {
        return this.agencyRepository.findById(id).orElseThrow(() ->{
            throw new RuntimeException("Agência não encontrada!");
        });
    }

    public List<Agency> getAllAgencies(Pageable pageable) {
        return this.agencyRepository.findAll((Sort) pageable);
    }

}
