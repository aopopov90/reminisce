package com.home.reminisce.service;

import com.home.reminisce.api.model.CommentRequest;
import com.home.reminisce.model.Comment;
import com.home.reminisce.repository.CommentRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public Comment createComment(CommentRequest commentRequest) {
        return commentRepository.save(Comment.builder()
                .sessionId(commentRequest.sessionId())
                .text(commentRequest.text())
                .categoryId(commentRequest.categoryId())
                .authoredBy(SecurityContextHolder.getContext().getAuthentication().getName())
                .createdOn(Instant.now())
                .build());
    }

    @Override
    public void deleteComment(Long id) {
        Optional<Comment> commentOptional = commentRepository.findById(id);
        if (commentOptional.isPresent()) {
            Comment comment = commentOptional.get();
            commentRepository.delete(comment);
        } else {
            throw new NoSuchElementException("Comment not found with ID" + id);
        }
    }

    @Override
    public Comment updateComment(CommentRequest commentRequest, Long id) {
        Optional<Comment> commentOptional = commentRepository.findById(id);
        if (commentOptional.isPresent()) {
            Comment comment = commentOptional.get();
            comment.setText(commentRequest.text());
            comment.setCategoryId(commentRequest.categoryId());
            return commentRepository.save(comment);
        } else {
            throw new NoSuchElementException("Comment not found with ID" + id);
        }
    }
}
