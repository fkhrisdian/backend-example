package com.kaspro.bank.persistance.repository;

import com.kaspro.bank.persistance.domain.TransferLimit;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferLimitRepository extends BaseRepository<TransferLimit>{
//    @Query(value="SELECT * FROM kasprobank.TRANSFER_LIMIT where partner_id=?1 AND tier_type=?2",
//            nativeQuery = true)
//    List<TransferLimit> findByPartnerIDAndTier(int id, String tier);

    @Query(value="SELECT DISTINCT(tier_type) FROM kasprobank.TRANSFER_LIMIT",
            nativeQuery = true)
    List<String> findTiers();

    @Query(value="SELECT * FROM kasprobank.TRANSFER_LIMIT where TIER_TYPE=?1",
            nativeQuery = true)
    List<TransferLimit> findByTier(String tier);

    @Query(value="SELECT * FROM kasprobank.TRANSFER_LIMIT where TIER_TYPE=?1 and DEST=?2",
            nativeQuery = true)
    TransferLimit findByTierAndDest(String tier, String dest);

    @Query(value="UPDATE kasprobank.TRANSFER_LIMIT T set T.TRX_LIMIT=?2 WHERE T.id=?1",
            nativeQuery = true)
    void updateByTrxLimit(int id, String limit);
}
