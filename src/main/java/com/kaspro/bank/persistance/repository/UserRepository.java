package com.kaspro.bank.persistance.repository;

import com.kaspro.bank.persistance.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends BaseRepository<User> {
    @Query(value="SELECT * FROM kasprobank.USER where id=?1",
            nativeQuery = true)
    User findByUserId(String id);

    @Query(value="SELECT * FROM kasprobank.USER where username=?1",
            nativeQuery = true)
    User findByUsername(String username);

    @Query(value="SELECT * FROM kasprobank.USER where email=?1",
            nativeQuery = true)
    User findByEmail(String email);

    @Query(value="SELECT * FROM kasprobank.USER where RESET_TOKEN=?1",
            nativeQuery = true)
    User findByToken(String token);

    @Query(value="SELECT * FROM kasprobank.USER where ROLES=?1",
            nativeQuery = true)
    List<User> findByRoleId(String id);
}
