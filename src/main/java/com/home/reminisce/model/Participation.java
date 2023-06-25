package com.home.reminisce.model;

import jakarta.persistence.Entity;
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
public class Participation {

    private long sessionId;

    private String participantName;

    private String addedBy;

    private Instant addedAt;
}
