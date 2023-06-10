package com.home.reminisce.model;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.List;

@Entity
public class Comment {
    @Id
    @GeneratedValue
    private Long id;

    private Long sessionId;

    private String authoredBy;

    private Timestamp createdOn;

    private String text;

    private Integer categoryId;

    @OneToMany
    @JoinColumn(name = "commentId")
    private List<Reaction> reactions;
}
