package com.kaspro.bank.persistance.repository;

import com.kaspro.bank.persistance.domain.TransactionHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionHistoryRepository extends BaseRepository<TransactionHistory>{

    @Query(value="SELECT * FROM kasprobank.TRANSACTION_HISTORY where TID=?1 and STATUS='Pending' and SKU=?2",
            nativeQuery = true)
    TransactionHistory findByTID(String tid, String sku);
}
