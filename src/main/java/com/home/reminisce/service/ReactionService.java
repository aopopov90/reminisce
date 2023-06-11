package com.home.reminisce.service;

import com.home.reminisce.api.model.ReactionRequest;
import com.home.reminisce.model.Reaction;
import org.springframework.http.ResponseEntity;

public interface ReactionService {
    ResponseEntity<Reaction> createReaction(ReactionRequest reactionRequest);

    void deleteReaction(Long reactionId);
}
