package com.kaspro.bank.persistance.repository;

import com.kaspro.bank.persistance.domain.BlacklistMsisdn;
import com.kaspro.bank.persistance.domain.Role;
import org.springframework.data.jpa.repository.Query;

public interface RoleRepository extends BaseRepository<Role>{

    @Query(value="SELECT * FROM kasprobank.ROLE where id=?1",
            nativeQuery = true)
    Role findByRoleId(String id);
}
