package com.home.reminisce.service;

import com.home.reminisce.api.model.ReactionRequest;
import com.home.reminisce.model.Reaction;
import com.home.reminisce.repository.ReactionRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ReactionServiceImpl implements ReactionService {

    private final ReactionRepository reactionRepository;

    public ReactionServiceImpl(ReactionRepository reactionRepository) {
        this.reactionRepository = reactionRepository;
    }

    @Override
    public Reaction createReaction(ReactionRequest reactionRequest) {
        return reactionRepository.save(Reaction.builder()
                .createdOn(Instant.now())
                .reactionType(reactionRequest.reactionType())
                .authoredBy(SecurityContextHolder.getContext().getAuthentication().getName())
                .commentId(reactionRequest.commentId())
                .build());
    }

    @Override
    public void deleteReaction(Long reactionId) {
        Optional<Reaction> optionalReaction = reactionRepository.findById(reactionId);
        if (optionalReaction.isPresent()) {
            reactionRepository.delete(optionalReaction.get());
        } else {
            throw new NoSuchElementException("Reaction not found with ID" + reactionId);
        }
    }
}
