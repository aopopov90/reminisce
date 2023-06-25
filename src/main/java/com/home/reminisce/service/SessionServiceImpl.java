package com.home.reminisce.service;

import com.home.reminisce.api.model.SessionRequest;
import com.home.reminisce.exceptions.UnauthorizedAccessException;
import com.home.reminisce.model.Session;
import com.home.reminisce.model.SessionStatus;
import com.home.reminisce.repository.SessionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;

    public SessionServiceImpl(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public Session findById(long id) {
        Optional<Session> optionalSession = sessionRepository.findById(id);

        if (optionalSession.isPresent()) {
            return optionalSession.get();
        } else {
            throw new EntityNotFoundException("Session not found with ID: " + id);
        }
    }

    @Override
    public boolean sessionExists(long sessionId) {
        return sessionRepository.findById(sessionId).isPresent();
    }

    @Override
    public List<Session> getAll() {
        return sessionRepository.findAll();
    }

    public Session createSession(SessionRequest sessionRequest) {
        return sessionRepository.save(Session.builder()
                .name(sessionRequest.name())
                .createdBy(SecurityContextHolder.getContext().getAuthentication().getName())
                .status(SessionStatus.IN_PROGRESS)
                .createdOn(Instant.now())
                .build());
    }

    @Override
    public Session updateSessionStatus(long id, SessionStatus status) {
        Session session = findById(id);

        if (!isAuthorizedToEditSession(session)) {
            throw new UnauthorizedAccessException("You are not authorized to update the status of this session.");
        }

        if (!Objects.equals(session.getStatus(), SessionStatus.COMPLETED)) {
            session.setStatus(status);
            session.setEndedOn(Instant.now());
            return sessionRepository.save(session);
        } else {
            return session;
        }
    }

    @Override
    public List<String> addParticipants(Long sessionId, List<String> participants) {
        Session session = findById(sessionId);

        if (!isAuthorizedToEditSession(session)) {
            throw new UnauthorizedAccessException("You are not authorized to add participants to this session.");
        }

        List<String> updatedParticipants = new ArrayList<>();
        updatedParticipants.addAll(Optional.ofNullable(session.getParticipants()).orElse(Collections.emptyList()));
        updatedParticipants.addAll(participants);
        session.setParticipants(updatedParticipants);
        sessionRepository.save(session);
        return updatedParticipants;
    }

    private boolean isAuthorizedToEditSession(Session session) {
        String authenticatedUser = SecurityContextHolder.getContext().getAuthentication().getName();
        return Optional.ofNullable(session.getCreatedBy()).orElse("").equals(authenticatedUser)
                || Optional.ofNullable(session.getParticipants()).orElse(Collections.emptyList()).contains(authenticatedUser);
    }


}
