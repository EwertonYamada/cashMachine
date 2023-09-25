package com.cashMachine.agency.dtos;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class AgencyDto {
    @NotNull
    private String name;
    @NotNull
    private Long number;
    @NotNull
    private Long bankId;

}
