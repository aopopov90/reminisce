package com.home.reminisce.service;

import com.home.reminisce.api.model.CommentRequest;
import com.home.reminisce.exceptions.UnauthorizedAccessException;
import com.home.reminisce.model.Comment;
import com.home.reminisce.model.Session;
import com.home.reminisce.repository.CommentRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final SessionService sessionService;

    private final ParticipationService participationService;

    public CommentServiceImpl(CommentRepository commentRepository, SessionService sessionService, ParticipationService participationService) {
        this.commentRepository = commentRepository;
        this.sessionService = sessionService;
        this.participationService = participationService;
    }

    @Override
    public Comment createComment(CommentRequest commentRequest) {
        Session session = sessionService.findById(commentRequest.sessionId());
        if (!isAuthorizedToComment(session)) {
            throw new UnauthorizedAccessException("You are not authorized to comment in this session");
        }
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
            if (comment.getAuthoredBy().equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
                commentRepository.delete(commentOptional.get());
            } else {
                throw new UnauthorizedAccessException("You are not authorized to delete this comment.");
            }
        } else {
            throw new NoSuchElementException("Comment not found with ID" + id);
        }
    }

    @Override
    public Comment updateComment(CommentRequest commentRequest, Long id) {
        Optional<Comment> commentOptional = commentRepository.findById(id);
        if (commentOptional.isPresent()) {
            Comment comment = commentOptional.get();
            if (comment.getAuthoredBy().equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
                comment.setText(commentRequest.text());
                comment.setCategoryId(commentRequest.categoryId());
                return commentRepository.save(comment);
            } else {
                throw new UnauthorizedAccessException("You are not authorized to update this comment.");
            }
        } else {
            throw new NoSuchElementException("Comment not found with ID" + id);
        }
    }

    private boolean isAuthorizedToComment(Session session) {
        String authenticatedUser = SecurityContextHolder.getContext().getAuthentication().getName();
        return Optional.ofNullable(session.getCreatedBy()).orElse("").equals(authenticatedUser)
                || participationService.getParticipations(session.getId()).stream()
                .anyMatch(participation -> participation.getParticipantName().equals(authenticatedUser));
    }
}
