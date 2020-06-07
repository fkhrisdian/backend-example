package com.kaspro.bank.persistance.repository;

import com.kaspro.bank.persistance.domain.DataPIC;
import com.kaspro.bank.persistance.domain.VirtualAccount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VirtualAccountRepository extends BaseRepository<VirtualAccount>{
    @Query(value="SELECT * FROM kasprobank.VIRTUAL_ACCOUNT where partner_id=?1",
            nativeQuery = true)
    VirtualAccount findByPartnerID(int id);
}
