package com.kaspro.bank.persistance.repository;

import com.kaspro.bank.persistance.domain.TrailAudit;
import com.kaspro.bank.persistance.domain.UsageAccumulator;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsageAccumulatorRepository extends BaseRepository<UsageAccumulator> {
    @Query(value="SELECT * FROM kasprobank.USAGE_ACCUMULATOR where owner_id=?1",
            nativeQuery = true)
    List<UsageAccumulator> findByOwnerID(int id);

    @Query(value="SELECT * FROM kasprobank.USAGE_ACCUMULATOR where owner_id=?1 and DEST=?2",
            nativeQuery = true)
    UsageAccumulator findByOwnerIDAndDest(int id, String dest);
}
