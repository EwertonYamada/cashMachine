package com.cashMachine.associate.associateAPIs;

import com.cashMachine.agency.agency.Agency;
import com.cashMachine.agency.dtos.AgencyDto;
import com.cashMachine.associate.associate.Associate;
import com.cashMachine.associate.associateServices.AssociateService;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;
import java.util.List;

@RestController
@RequestMapping("/associate")
public class AssociateAPI {
    private final AssociateService associateService;

    public AssociateAPI(AssociateService associateService) {
        this.associateService = associateService;
    }

    @PostMapping("/new")
    public ResponseEntity<Associate> createAssociate(@RequestBody Associate associate) {
        return ResponseEntity.ok(this.associateService.createAssociate(associate));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Associate> getAssociateById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(this.associateService.getAssociateById(id));
    }

    @GetMapping("/all-associates")
    public ResponseEntity<List<Associate>> getAllAssociates() {
        return ResponseEntity.ok(this.associateService.getAllAssociates());
    }
}
