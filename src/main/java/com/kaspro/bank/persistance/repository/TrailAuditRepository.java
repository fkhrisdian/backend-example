package com.kaspro.bank.persistance.repository;

import com.kaspro.bank.persistance.domain.Lampiran;
import com.kaspro.bank.persistance.domain.TrailAudit;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TrailAuditRepository extends BaseRepository<TrailAudit>{
    @Query(value="SELECT * FROM kasprobank.AUDIT_TRAIL where owner_id=?1 order by  kasprobank.AUDIT_TRAIL.DATE_CREATED desc",
            nativeQuery = true)
    List<TrailAudit> findByOwnerID(String id);
}
