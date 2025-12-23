package com.kousta.accountservicecqrses.commonapi.dto;

public record CreditAccountDTO(
        String accountId,
        double amount,
        String currency
) {
}