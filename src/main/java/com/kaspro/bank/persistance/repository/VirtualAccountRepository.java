package com.kaspro.bank.persistance.repository;

import com.kaspro.bank.persistance.domain.DataPIC;
import com.kaspro.bank.persistance.domain.VirtualAccount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VirtualAccountRepository extends BaseRepository<VirtualAccount>{
    @Query(value="SELECT * FROM kasprobank.VIRTUAL_ACCOUNT where owner_id=?1 and status='ACTIVE'",
            nativeQuery = true)
    VirtualAccount findByPartnerID(int id);

    @Query(value="SELECT msisdn FROM kasprobank.VIRTUAL_ACCOUNT where msisdn=?1 and status='ACTIVE' and flag='I'",
            nativeQuery = true)
    List<String>  findMsisdn(String msisdn);

    @Query(value="SELECT * FROM kasprobank.VIRTUAL_ACCOUNT where id=?1",
            nativeQuery = true)
    List<VirtualAccount> findVA(int id);

    @Query(value="SELECT va FROM kasprobank.VIRTUAL_ACCOUNT where va=?1",
            nativeQuery = true)
    List<String> findVAs(String va);
}
