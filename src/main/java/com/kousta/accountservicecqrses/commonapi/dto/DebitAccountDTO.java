package com.kousta.accountservicecqrses.commonapi.dto;


public record DebitAccountDTO(
        String accountId,
        double amount,
        String currency
){}