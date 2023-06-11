package com.home.reminisce.api.model;

import com.home.reminisce.model.ReactionType;

public record ReactionRequest(Long commentId, ReactionType reactionType) {}
