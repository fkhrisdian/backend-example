package com.kaspro.bank.persistance.repository;

import com.kaspro.bank.persistance.domain.TransactionHistory;
import com.kaspro.bank.persistance.domain.TransactionHistoryStaging;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionHistoryStagingRepository extends BaseRepository<TransactionHistoryStaging>{

    @Query(value="SELECT * FROM kasprobank.TRANSACTION_HISTORY_STG where TID=?1 and STATUS='Pending' and SKU=?2",
            nativeQuery = true)
    TransactionHistoryStaging findByTID(String tid, String sku);

    @Query(value="SELECT * FROM kasprobank.TRANSACTION_HISTORY_STG where TID=?1 and STATUS='Pending' and SKU not in('KasproBank','Kaspro','Emoney')",
            nativeQuery = true)
    TransactionHistoryStaging findOtherBank(String tid);

    @Query(value="SELECT * FROM kasprobank.TRANSACTION_HISTORY_STG where sender_id=?1 limit 1",
            nativeQuery = true)
    TransactionHistoryStaging findBySenderId(String id);
}
