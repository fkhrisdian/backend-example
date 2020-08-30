package com.kaspro.bank.persistance.repository;

import com.kaspro.bank.persistance.domain.Partner;
import com.kaspro.bank.persistance.domain.PartnerMember;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PartnerMemberRepository extends BaseRepository<PartnerMember>{
    @Query(value="SELECT * FROM kasprobank.PARTNER_MEMBER where id=?1",
            nativeQuery = true)
    PartnerMember findPartnerMember(int id);

    @Query(value="SELECT count(id) FROM kasprobank.PARTNER_MEMBER where DATE_CREATED>=DATE(NOW()) - INTERVAL :days DAY;",
            nativeQuery = true)
    String findByLastDays(String days);

    @Query(value="SELECT * FROM kasprobank.PARTNER_MEMBER where id=?1",
          nativeQuery = true)
    List<PartnerMember> findListPartnerMember(int id);

    @Query(value="SELECT * FROM kasprobank.PARTNER_MEMBER where PARTNER_ALIAS=?1",
            nativeQuery = true)
    PartnerMember findByPartner(String alias);

    @Query(value="SELECT NAMA FROM kasprobank.PARTNER_MEMBER where NAMA=?1",
            nativeQuery = true)
    List<String> findName(String name);

    @Transactional
    @Modifying
    @Query(value="update kasprobank.PARTNER_MEMBER set status=?1 where id=?2",
            nativeQuery = true)
    void udpateStatus(String status, int id);

    @Query(value="SELECT kasprobank.PARTNER_MEMBER.id\n" +
            "FROM kasprobank.PARTNER_MEMBER\n" +
            "INNER JOIN kasprobank.TRANSFER_INFO_MEMBER ON kasprobank.PARTNER_MEMBER.id = kasprobank.TRANSFER_INFO_MEMBER.owner_id\n" +
            "where kasprobank.PARTNER_MEMBER.PARTNER_ALIAS=?2 and kasprobank.TRANSFER_INFO_MEMBER.name='TierLimit' and kasprobank.TRANSFER_INFO_MEMBER.value=?1",
            nativeQuery = true)
    List<Integer> findUsedTier(String tier, String alias);
}
