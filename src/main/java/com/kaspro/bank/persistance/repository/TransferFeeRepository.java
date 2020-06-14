package com.kaspro.bank.persistance.repository;

import com.kaspro.bank.persistance.domain.Partner;
import com.kaspro.bank.persistance.domain.TransferFee;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransferFeeRepository extends BaseRepository<TransferFee>{
    @Query(value="SELECT * FROM kasprobank.TRANSFER_FEE where owner_id=?1",
            nativeQuery = true)
    List<TransferFee> findByPartnerID(int id);

    @Query(value="SELECT * FROM kasprobank.TRANSFER_FEE where owner_id=?1 and dest=?2",
            nativeQuery = true)
    TransferFee findDetail(int id, String Dest);

}
