package com.kaspro.bank.persistance.repository;

import com.kaspro.bank.persistance.domain.TransactionHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query(value="SELECT * FROM kasprobank.TRANSACTION_HISTORY th WHERE th.acc_type = :accType AND (:partnerId is null or th.partner_id = :partnerId) AND (:senderId is null or th.sender_id = :senderId) AND (:msisdn is null or th.msisdn = :msisdn) AND (:tid is null or th.tid = :tid)\n" +
            "union all\n" +
            "SELECT * FROM kasprobank.TRANSACTION_HISTORY_STG ths WHERE ths.acc_type = :accType AND (:partnerId is null or ths.partner_id = :partnerId) AND (:senderId is null or ths.sender_id = :senderId) AND (:msisdn is null or ths.msisdn = :msisdn) AND (:tid is null or ths.tid = :tid)",
            nativeQuery = true)
    List<TransactionHistory> findFilteredTransaction(@Param("accType") String accType,
                                                     @Param("partnerId") String partnerId,
                                                     @Param("senderId") String senderId,
                                                     @Param("msisdn") String msisdn,
                                                     @Param("tid") String id);

}
