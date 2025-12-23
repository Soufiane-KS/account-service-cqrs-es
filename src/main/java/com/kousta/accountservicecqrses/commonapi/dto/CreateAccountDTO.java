package com.kousta.accountservicecqrses.commonapi.dto;

public record CreateAccountDTO(
        String currency,
        double initialBalance
) {
}