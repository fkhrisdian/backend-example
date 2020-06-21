package com.kaspro.bank.persistance.repository;

import com.kaspro.bank.persistance.domain.PartnerMemberToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PartnerMemberTokenRepository extends JpaRepository<PartnerMemberToken,Long> {
    @Query(value="SELECT * FROM kasprobank.PARTNER_MEMBER_TOKEN where PARTNER_CODE=?1 ORDER BY PARTNER_MEMBER_CODE ASC LIMIT 1",
            nativeQuery = true)
    PartnerMemberToken findMinId(String partnerCode);
}
