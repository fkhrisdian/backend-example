package com.kaspro.bank.persistance.repository;

import com.kaspro.bank.persistance.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> {
}
