package com.be.sample.persistance.repository;

import com.be.sample.persistance.domain.Base;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;


@NoRepositoryBean
public interface BaseRepository<T extends Base> extends JpaRepository<T, Integer>, JpaSpecificationExecutor<T> {

    Optional<T> findBySecureId(String secureId);
}
