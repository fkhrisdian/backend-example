package com.kaspro.bank.persistance.repository;

import com.kaspro.bank.persistance.domain.RequestCard;
import org.springframework.data.jpa.repository.Query;

public interface RequestCardRepository extends BaseRepository<RequestCard>{

    @Query(value="SELECT * FROM kasprobank.REQUEST_CARD where id=?1",
            nativeQuery = true)
    RequestCard findByRequestID(String id);
}
