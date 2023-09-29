package com.cashMachine.transaction.enums;

public enum TransactionType {
    DEPOSIT("depósito"),
    WITHDRAW("saque"),
    TRANSFER("transferência"),
    RESCUE("resgate");

    TransactionType(String description) {
    }

}
