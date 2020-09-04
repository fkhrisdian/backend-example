package com.kaspro.bank.persistance.repository;

import com.kaspro.bank.persistance.domain.FileConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FileConfigRepository extends JpaRepository<FileConfig, Integer> {
  Optional<FileConfig> findById(int id);

  @Query(value = "select k.param_name FROM kasprobank.FILE_CONFIG k WHERE k.param_name LIKE :prefix%", nativeQuery = true)
  List<String> selectNameByPrefix(String prefix);

  @Query(value = "select * FROM kasprobank.FILE_CONFIG k WHERE k.param_name LIKE :prefix%", nativeQuery = true)
  List<FileConfig> selectByPrefix(String prefix);

  @Query(value = "select * FROM kasprobank.FILE_CONFIG k WHERE k.param_name=?1", nativeQuery = true)
  FileConfig selectByParamName(String name);
}
