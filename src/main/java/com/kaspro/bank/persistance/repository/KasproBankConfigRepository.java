package com.kaspro.bank.persistance.repository;

import com.kaspro.bank.persistance.domain.KasprobankConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.Id;

public interface KasproBankConfigRepository extends JpaRepository<KasprobankConfig, Integer> {

}
