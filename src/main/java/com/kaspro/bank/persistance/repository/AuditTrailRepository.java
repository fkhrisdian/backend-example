package com.kaspro.bank.persistance.repository;

import com.kaspro.bank.persistance.domain.AuditTrail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;

@Repository
public interface AuditTrailRepository extends JpaRepository<AuditTrail, Date> {
}
