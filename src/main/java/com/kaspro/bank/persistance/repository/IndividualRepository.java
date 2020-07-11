package com.kaspro.bank.persistance.repository;

import com.kaspro.bank.persistance.domain.Individual;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IndividualRepository extends BaseRepository<Individual>{

    @Query(value="SELECT * FROM kasprobank.INDIVIDUAL where id=?1",
            nativeQuery = true)
    Individual findIndividual(int id);

    @Query(value="SELECT * FROM kasprobank.INDIVIDUAL where MSISDN=?1 and STATUS='ACTIVE'",
            nativeQuery = true)
    Individual findByMsisdn(String msisdn);
}
