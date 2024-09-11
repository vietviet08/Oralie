package com.oralie.accounts.repository;

import ch.qos.logback.core.net.SMTPAppenderBase;
import com.oralie.accounts.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountsRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUsername(String username);

    Optional<Account> findByUserId(String userId);
}
