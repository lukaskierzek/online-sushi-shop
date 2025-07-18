package com.sushiShop.onlineSushiShop.service;

import com.sushiShop.onlineSushiShop.exception.CommentNotFoundException;
import com.sushiShop.onlineSushiShop.model.Comment;
import com.sushiShop.onlineSushiShop.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
            .orElseThrow(() -> new CommentNotFoundException(String.format("Comment with ID %s was not found", commentId)));
    }
}
