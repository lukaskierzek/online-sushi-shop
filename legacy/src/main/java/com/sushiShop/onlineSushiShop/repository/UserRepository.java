package com.sushiShop.onlineSushiShop.repository;

import com.sushiShop.onlineSushiShop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT * FROM users WHERE user_name = :username", nativeQuery = true)
    Optional<User> findByUserName(@Param("username") String username);

}
