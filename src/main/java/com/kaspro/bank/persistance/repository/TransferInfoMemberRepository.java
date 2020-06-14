package com.kaspro.bank.persistance.repository;

import com.kaspro.bank.persistance.domain.Lampiran;
import com.kaspro.bank.persistance.domain.TransferInfoMember;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferInfoMemberRepository extends BaseRepository<TransferInfoMember>{
    @Query(value="SELECT * FROM kasprobank.TRANSFER_INFO_MEMBER where owner_id=?1",
            nativeQuery = true)
    List<TransferInfoMember> findByPartnerID(int id);

    @Query(value="SELECT * FROM kasprobank.TRANSFER_INFO_MEMBER where owner_id=?1 and name=?2",
            nativeQuery = true)
    TransferInfoMember findDetail(int id, String name);
}
