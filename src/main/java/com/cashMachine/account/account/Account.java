package com.cashMachine.account.account;

import com.cashMachine.agency.agency.Agency;
import com.cashMachine.associate.associate.Associate;
import com.cashMachine.bank.bank.Bank;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Data
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
    @Column(name = "type_account")
    private String typeAccount;
}
