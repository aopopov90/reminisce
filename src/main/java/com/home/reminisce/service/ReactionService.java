package com.home.reminisce.service;

import com.home.reminisce.api.model.ReactionRequest;
import com.home.reminisce.model.Reaction;

public interface ReactionService {
    Reaction createReaction(ReactionRequest reactionRequest);
    void deleteReaction(Long reactionId);
}
