package com.home.reminisce.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reaction {
    @Id
    @GeneratedValue
    private Long id;

    private Long commentId;

    private String authoredBy;

    private Instant createdOn;

    @Enumerated(EnumType.STRING)
    private ReactionType reactionType;
}
