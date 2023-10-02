package com.cashMachine.transaction.dtos;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class TransactionDto {
    private BigDecimal value;
    private Long sourceAccount;
    private Long targetAccount;
}
