package com.kaspro.bank.persistance.repository;

import com.kaspro.bank.persistance.domain.KasprobankConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface KasproBankConfigRepository extends JpaRepository<KasprobankConfig, Integer> {
  Optional<KasprobankConfig> findById(int id);
}
