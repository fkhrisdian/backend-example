package com.kaspro.bank.persistance.repository;

import com.kaspro.bank.persistance.domain.Lampiran;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LampiranRepository extends BaseRepository<Lampiran>{
    @Query(value="SELECT * FROM kasprobank.LAMPIRAN where owner_id=?1",
            nativeQuery = true)
    List<Lampiran> findByPartnerID(int id);

    @Query(value="SELECT * FROM kasprobank.LAMPIRAN where owner_id=?1 and name=?2",
            nativeQuery = true)
    Lampiran findDetail(int id, String name);
}
