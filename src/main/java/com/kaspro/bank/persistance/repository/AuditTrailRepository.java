package com.kaspro.bank.persistance.repository;

import com.kaspro.bank.persistance.domain.AuditTrail;
import com.kaspro.bank.persistance.domain.DataPIC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface AuditTrailRepository extends BaseRepository<AuditTrail> {
    @Query(value="SELECT * FROM kasprobank.KASPROBANKAPP_AUDIT_TRAIL where owner_id=?1",
            nativeQuery = true)
    List<AuditTrail> findByPartnerID(int id);
}
