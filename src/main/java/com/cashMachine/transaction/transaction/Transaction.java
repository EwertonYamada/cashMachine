package com.cashMachine.transaction.transaction;

import com.cashMachine.account.account.Account;
import com.cashMachine.transaction.enums.TransactionType;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "transaction_type")
    private String transactionType;
    @Column(name = "transaction_value")
    private BigDecimal value;
    @Column(name = "transaction_date")
    private Date date;
    @JoinColumn(name = "source_account")
    @OneToOne
    private Account sourceAccount;
    @JoinColumn(name = "target_account")
    @OneToOne
    private Account targetAccount;
}
