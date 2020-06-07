package com.kaspro.bank.persistance.repository;

import com.kaspro.bank.persistance.domain.DataPIC;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataPICRepository extends BaseRepository<DataPIC>{
    @Query(value="SELECT * FROM kasprobank.DATA_PIC where partner_id=?1",
            nativeQuery = true)
    DataPIC findByPartnerID(int id);
}
