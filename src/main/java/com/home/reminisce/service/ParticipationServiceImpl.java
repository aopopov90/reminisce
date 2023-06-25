package com.home.reminisce.service;

import com.home.reminisce.exceptions.UnauthorizedAccessException;
import com.home.reminisce.model.Participation;
import com.home.reminisce.model.Session;
import com.home.reminisce.repository.ParticipationRepository;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


public class ParticipationServiceImpl implements ParticipationService {

    private final SessionService sessionService;

    private final ParticipationRepository participationRepository;

    public ParticipationServiceImpl(SessionService sessionService, ParticipationRepository participationRepository) {
        this.sessionService = sessionService;
        this.participationRepository = participationRepository;
    }

    @Override
    public List<Participation> addParticipations(Long sessionId, List<String> participants) {
        Session session = sessionService.findById(sessionId);
        if (Optional.ofNullable(session).isEmpty()) {
            throw new NoSuchElementException("Session not found with ID: " + 1);
        }

        if (!isAuthorizedToEditSession(session)) {
            throw new UnauthorizedAccessException("You are not authorized to add participants to this session.");
        }

        List<Participation> participations = participants.stream()
                .map(participant -> new Participation(sessionId, participant,
                        SecurityContextHolder.getContext().getAuthentication().getName(), Instant.now()))
                .collect(Collectors.toList());
        return participationRepository.saveAll(participations);
    }

    @Override
    public List<Participation> getParticipations(Long sessionId) {
        return participationRepository.findBySessionId(Collections.singleton(sessionId));
    }

    @Override
    public void deleteParticipations(Long sessionId, List<String> participants) {
        Session session = sessionService.findById(sessionId);
        if (Optional.ofNullable(session).isEmpty()) {
            throw new NoSuchElementException("Session not found with ID: " + 1);
        }

        if (!isAuthorizedToEditSession(session)) {
            throw new UnauthorizedAccessException("You are not authorized to delete participants from this session.");
        }

        participationRepository.deleteBySessionIdAndParticipantNameIn(sessionId, participants);
    }

    private boolean isAuthorizedToEditSession(Session session) {
        String authenticatedUser = SecurityContextHolder.getContext().getAuthentication().getName();
        return Optional.ofNullable(session.getCreatedBy()).orElse("").equals(authenticatedUser)
                || getParticipations(session.getId()).stream()
                    .anyMatch(participation -> participation.getParticipantName().equals(authenticatedUser));
    }
}
