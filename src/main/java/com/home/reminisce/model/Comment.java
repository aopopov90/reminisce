package com.home.reminisce.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.sql.Timestamp;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long sessionId;

    private String authoredBy;

    private Timestamp createdOn;

    private String text;

    private Integer votesPositive;

    private Integer votesNegative;

    private Integer categoryId;
}
