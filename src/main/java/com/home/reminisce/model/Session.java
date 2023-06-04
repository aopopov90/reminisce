package com.home.reminisce.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    private Instant createdOn;

    private Instant endedOn;

    @Enumerated(EnumType.STRING)
    private SessionStatus status;

    private List<String> participants;

    @OneToMany
    @JoinColumn(name = "sessionId")
    private List<Comment> comments;
}
