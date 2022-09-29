package com.backendtestka.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountModelRepository extends JpaRepository<AccountModel, UUID> {

    boolean existsByUsername(String username);

    AccountModel findByUsername(String username);
}