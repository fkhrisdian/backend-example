package com.kaspro.bank.persistance.repository;

import com.kaspro.bank.persistance.domain.TransactionHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionHistoryRepository extends BaseRepository<TransactionHistory>{

    @Query(value="SELECT * FROM kasprobank.TRANSACTION_HISTORY where TID=?1 and STATUS='Pending' and SKU=?2",
            nativeQuery = true)
    TransactionHistory findByTID(String tid, String sku);

    @Query(value="SELECT * FROM kasprobank.TRANSACTION_HISTORY\n" +
            "union all\n" +
            "SELECT * FROM kasprobank.TRANSACTION_HISTORY_STG",
            nativeQuery = true)
    List<TransactionHistory> findEntireTransaction();

}
