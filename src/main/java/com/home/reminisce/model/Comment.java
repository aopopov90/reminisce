package com.home.reminisce.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    @Id
    @GeneratedValue
    private Long id;

    private Long sessionId;

    private String authoredBy;

    private Instant createdOn;

    private String text;

    private Integer categoryId;

    @OneToMany
    @JoinColumn(name = "commentId")
    private List<Reaction> reactions;
}
