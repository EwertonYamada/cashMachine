package com.cashMachine.bank.bank;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Data
@Table(name = "bank")
public class Bank {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @NotNull
    @Column(name = "bank_number")
    private Long bankNumber;
    @NotNull
    @Column(name = "bank_name")
    private String bankName;
    @NotNull
    @Column(name = "full_balance_transaction")
    private BigDecimal fullBalanceTransaction;
}
