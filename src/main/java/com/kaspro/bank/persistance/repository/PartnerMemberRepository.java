package com.kaspro.bank.persistance.repository;

import com.kaspro.bank.persistance.domain.Partner;
import com.kaspro.bank.persistance.domain.PartnerMember;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PartnerMemberRepository extends BaseRepository<PartnerMember>{
    @Query(value="SELECT * FROM kasprobank.PARTNER_MEMBER where id=?1",
            nativeQuery = true)
    PartnerMember findPartnerMember(int id);
}
