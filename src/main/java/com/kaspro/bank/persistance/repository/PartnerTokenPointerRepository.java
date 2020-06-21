package com.kaspro.bank.persistance.repository;

import com.kaspro.bank.persistance.domain.PartnerTokenPointer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartnerTokenPointerRepository extends JpaRepository<PartnerTokenPointer,Long> {
}
