package com.kaspro.bank.persistance.repository;

import com.kaspro.bank.persistance.domain.KasprobankConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface KasproBankConfigRepository extends JpaRepository<KasprobankConfig, Integer> {
  Optional<KasprobankConfig> findById(int id);

  @Query(value = "select k.param_name FROM kasprobank.KASPROBANKAPP_CONFIG k WHERE k.param_name LIKE :prefix%", nativeQuery = true)
  List<String> selectNameByPrefix(String prefix);

  @Query(value = "select * FROM kasprobank.KASPROBANKAPP_CONFIG k WHERE k.param_name LIKE :prefix%", nativeQuery = true)
  List<KasprobankConfig> selectByPrefix(String prefix);
}
