package com.home.reminisce.service;

import com.home.reminisce.model.Participation;

import java.util.List;

public interface ParticipationService {
    List<Participation> addParticipations(Long sessionId, List<String> participants);

    List<Participation> getParticipations(Long sessionId);

    void deleteParticipations(Long sessionId, List<String> participants);
}
