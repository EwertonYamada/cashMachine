package com.cashMachine.account.dtos;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class AccountDto {
    @NotNull
    private Long number;
    @NotNull
    private Long agency;
    @NotNull
    private Long associate;
    @NotNull
    private String typeAccount;
}
