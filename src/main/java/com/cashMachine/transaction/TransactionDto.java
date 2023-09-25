package com.cashMachine.transaction;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class TransactionDto {
    private BigDecimal value;
    private Long sourceAccount;
    private Long targetAccount;
}
