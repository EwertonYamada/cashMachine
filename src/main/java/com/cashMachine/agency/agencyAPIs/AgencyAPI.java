package com.cashMachine.agency.agencyAPIs;

import com.cashMachine.agency.agency.Agency;
import com.cashMachine.agency.agencyServices.AgencyService;
import com.cashMachine.agency.dtos.AgencyDto;
import com.cashMachine.bank.bank.Bank;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;
import java.util.List;

@RestController
@RequestMapping("/agency")
public class AgencyAPI {
    final AgencyService agencyService;

    public AgencyAPI(AgencyService agencyService) {
        this.agencyService = agencyService;
    }

    @PostMapping("/new")
    public ResponseEntity<Agency> createAgency(@RequestBody AgencyDto agencyDto) {
        return ResponseEntity.ok(this.agencyService.createAgency(agencyDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Agency> getAgencyById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(this.agencyService.getAgencyById(id));
    }

    @GetMapping("/all-agencies")
    public ResponseEntity<List<Agency>> getAllAgencies() {
        return ResponseEntity.ok(this.agencyService.getAllAgencies());
    }
}
