package com.kaspro.bank.persistance.repository;

import com.kaspro.bank.persistance.domain.PartnerToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PartnerTokenRepository extends JpaRepository<PartnerToken, Long> {
    @Query(value="SELECT * FROM kasprobank.PARTNER_TOKEN where id=?1",
            nativeQuery = true)
    PartnerToken findPT(Long id);

    @Query(value="SELECT * FROM kasprobank.PARTNER_TOKEN where PARTNER_CODE=?1",
            nativeQuery = true)
    PartnerToken findPC(String pc);
}
