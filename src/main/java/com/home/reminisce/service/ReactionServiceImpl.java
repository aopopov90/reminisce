package com.home.reminisce.service;

import com.home.reminisce.api.model.ReactionRequest;
import com.home.reminisce.exceptions.UnauthorizedAccessException;
import com.home.reminisce.model.Reaction;
import com.home.reminisce.repository.ReactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ReactionServiceImpl implements ReactionService {

    private final ReactionRepository reactionRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ReactionServiceImpl(ReactionRepository reactionRepository, SimpMessagingTemplate messagingTemplate) {
        this.reactionRepository = reactionRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    @Transactional
    public ResponseEntity<Reaction> createReaction(ReactionRequest reactionRequest) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Reaction> reactions = reactionRepository.findByCommentIdAndAuthoredBy(
                reactionRequest.commentId(),
                userEmail
        );

        if (!reactions.isEmpty()) {
            if (reactions.get(0).getReactionType().equals(reactionRequest.reactionType())) {
                return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
            } else {
                reactionRepository.delete(reactions.get(0));
            }
        }

        Reaction createdReaction = reactionRepository.save(Reaction.builder()
                .createdOn(Instant.now())
                .reactionType(reactionRequest.reactionType())
                .authoredBy(userEmail)
                .commentId(reactionRequest.commentId())
                .build());

        // push new reaction to the websocket
        messagingTemplate.convertAndSend("/topic/newReaction", createdReaction);

        return new ResponseEntity<>(createdReaction, HttpStatus.CREATED);
    }

    @Override
    public void deleteReaction(Long reactionId) {
        Optional<Reaction> optionalReaction = reactionRepository.findById(reactionId);
        if (optionalReaction.isPresent()) {
            Reaction reaction = optionalReaction.get();
            if (reaction.getAuthoredBy().equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
                reactionRepository.delete(optionalReaction.get());
            } else {
                throw new UnauthorizedAccessException("You are not authorized to delete this reaction.");
            }
        } else {
            throw new NoSuchElementException("Reaction not found with ID" + reactionId);
        }
    }
}
