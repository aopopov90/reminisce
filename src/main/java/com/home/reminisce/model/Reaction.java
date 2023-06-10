package com.home.reminisce.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.sql.Timestamp;

public class Reaction {
    @Id
    @GeneratedValue
    private Long id;

    private Long commentId;

    private String authoredBy;

    private Timestamp createdOn;

    private ReactionType reactionType;
}
