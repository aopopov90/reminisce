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
    @GeneratedValue
    private long id;

    private String name;

    private Instant createdOn;

    private Instant endedOn;

    private String createdBy;

    @Enumerated(EnumType.STRING)
    private SessionStatus status;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "sessionId")
    private List<Comment> comments;
}
