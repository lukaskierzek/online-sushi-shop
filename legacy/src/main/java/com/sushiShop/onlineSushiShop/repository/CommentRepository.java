package com.sushiShop.onlineSushiShop.repository;

import com.sushiShop.onlineSushiShop.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
