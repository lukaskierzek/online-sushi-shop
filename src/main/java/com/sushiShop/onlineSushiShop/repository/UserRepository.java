package com.sushiShop.onlineSushiShop.repository;

import com.sushiShop.onlineSushiShop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
