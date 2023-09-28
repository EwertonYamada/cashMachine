package com.cashMachine.agency.agency;

import com.cashMachine.bank.bank.Bank;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "agency")
public class
Agency {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "agency_name")
    private String name;
    @Column(name = "agency_number")
    private Long number;
    @JoinColumn(name = "bank_id")
    @ManyToOne
    private Bank bank;
}
