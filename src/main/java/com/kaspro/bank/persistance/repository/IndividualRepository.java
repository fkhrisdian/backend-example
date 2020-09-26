package com.kaspro.bank.persistance.repository;

import com.kaspro.bank.persistance.domain.Individual;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IndividualRepository extends BaseRepository<Individual>{

    @Query(value="SELECT * FROM kasprobank.INDIVIDUAL where id=?1",
            nativeQuery = true)
    Individual findIndividual(int id);

    @Query(value="SELECT * FROM kasprobank.INDIVIDUAL where MSISDN=?1 and STATUS='ACTIVE'",
            nativeQuery = true)
    Individual findByMsisdn(String msisdn);

    @Query(value="SELECT * FROM kasprobank.INDIVIDUAL where MSISDN=?1",
            nativeQuery = true)
    Individual findByMsisdn2(String msisdn);

    @Query(value="SELECT * FROM kasprobank.INDIVIDUAL ORDER BY NAME ASC",
            nativeQuery = true)
    List<Individual> findAllSort();

    @Query(value="SELECT * FROM kasprobank.INDIVIDUAL ORDER BY NAME ASC LIMIT ?1",
            nativeQuery = true)
    List<Individual> findAllSortLimited(int limit);

    @Query(value="SELECT count(id) FROM kasprobank.INDIVIDUAL where DATE_CREATED>=DATE(NOW()) - INTERVAL :days DAY;",
            nativeQuery = true)
    String findByLastDays(String days);
}
