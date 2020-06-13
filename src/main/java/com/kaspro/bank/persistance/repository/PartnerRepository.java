package com.kaspro.bank.persistance.repository;

import com.kaspro.bank.persistance.domain.Partner;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PartnerRepository extends BaseRepository<Partner> {

    @Query(value="SELECT * FROM kasprobank.PARTNER where id=?1",
    nativeQuery = true)
    Partner findPartner(int id);

    @Query(value="SELECT * FROM kasprobank.PARTNER where alias=?1",
            nativeQuery = true)
    Partner findByAlias(String alias);
}