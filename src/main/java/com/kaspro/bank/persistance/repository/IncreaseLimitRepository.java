package com.kaspro.bank.persistance.repository;

import com.kaspro.bank.persistance.domain.IncreaseLimit;
import com.kaspro.bank.persistance.domain.TransferLimit;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface IncreaseLimitRepository extends BaseRepository<IncreaseLimit>{
//    @Query(value="SELECT * FROM kasprobank.TRANSFER_LIMIT where partner_id=?1 AND tier_type=?2",
//            nativeQuery = true)
//    List<TransferLimit> findByPartnerIDAndTier(int id, String tier);

    @Query(value="SELECT * FROM kasprobank.INCREASE_LIMIT where id=?1",
            nativeQuery = true)
    IncreaseLimit findByReqId(String id);

    @Query(value="SELECT * FROM kasprobank.INCREASE_LIMIT where member_id=?1 and destination=?2",
            nativeQuery = true)
    IncreaseLimit findByDest(String memberId, String dest);


    @Transactional
    @Modifying
    @Query(value="UPDATE kasprobank.INCREASE_LIMIT T set T.status=?2 WHERE T.id=?1",
            nativeQuery = true)
    void updateStatus(String id, String status);
}
