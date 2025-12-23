package com.kousta.accountservicecqrses.query.repo;

import com.kousta.accountservicecqrses.query.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account,String> {
}