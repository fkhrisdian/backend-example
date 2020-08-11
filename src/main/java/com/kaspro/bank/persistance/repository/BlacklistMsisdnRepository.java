package com.kaspro.bank.persistance.repository;

import com.kaspro.bank.persistance.domain.BlacklistMsisdn;
import org.springframework.data.jpa.repository.Query;

public interface BlacklistMsisdnRepository extends BaseRepository<BlacklistMsisdn>{

    @Query(value="SELECT * FROM kasprobank.BLACKLIST_MSISDN where id=?1",
            nativeQuery = true)
    BlacklistMsisdn findByBMId(String id);

    @Query(value="SELECT * FROM kasprobank.BLACKLIST_MSISDN where msisdn=?1",
            nativeQuery = true)
    BlacklistMsisdn findByMSISDN(String msisdn);
}
