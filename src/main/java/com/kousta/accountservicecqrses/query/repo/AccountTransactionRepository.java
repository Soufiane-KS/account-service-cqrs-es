package com.kousta.accountservicecqrses.query.repo;

import com.kousta.accountservicecqrses.query.entities.Account;
import com.kousta.accountservicecqrses.query.entities.AccountTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, Long> {
}