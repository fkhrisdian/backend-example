package com.kaspro.bank.persistance.repository;

import com.kaspro.bank.persistance.domain.TransferFee;
import com.kaspro.bank.persistance.domain.TransferLimit;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferLimitRepository extends BaseRepository<TransferLimit>{
    @Query(value="SELECT * FROM kasprobank.TRANSFER_LIMIT where partner_id=?1 AND tier_type=?2",
            nativeQuery = true)
    List<TransferLimit> findByPartnerIDAndTier(int id, String tier);

    @Query(value="SELECT DISTINCT(tier_type) FROM kasprobank.TRANSFER_LIMIT where partner_id=?1",
            nativeQuery = true)
    List<String> findTiersByPartnerID(int id);
}
