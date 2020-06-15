package com.kaspro.bank.persistance.repository;

import com.kaspro.bank.persistance.domain.Partner;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PartnerRepository extends BaseRepository<Partner> {

    @Query(value="SELECT * FROM kasprobank.PARTNER where id=?1",
    nativeQuery = true)
    List<Partner> findListPartner(int id);

    @Query(value="SELECT * FROM kasprobank.PARTNER where id=?1",
            nativeQuery = true)
    Partner findPartner(int id);

    @Query(value="SELECT alias FROM kasprobank.PARTNER where alias=?1",
            nativeQuery = true)
    List<String> findAlias(String alias);

    @Transactional
    @Modifying
    @Query(value="update kasprobank.PARTNER set tiers=?1 where id=?2",
            nativeQuery = true)
    void udpateTier(String tier, int id);

    @Transactional
    @Modifying
    @Query(value="update kasprobank.PARTNER set status=?1 where id=?2",
            nativeQuery = true)
    void udpateStatus(String status, int id);
}