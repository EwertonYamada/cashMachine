package com.cashMachine.account.account;

import com.cashMachine.agency.agency.Agency;
import com.cashMachine.associate.associate.Associate;
import com.cashMachine.bank.bank.Bank;
import liquibase.pro.packaged.S;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Data
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Column(name = "account_number")
    private Long Number;
    @JoinColumn(name = "agency_id")
    @ManyToOne
    private Agency agency;
    @JoinColumn(name = "associate_id")
    @ManyToOne
    private Associate associate;
    @Column(name = "balance")
    private BigDecimal balance;
    @Column(name = "account_type")
    private String accountType;
}
