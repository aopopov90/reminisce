package com.home.reminisce.service;

import com.home.reminisce.api.model.SessionRequest;
import com.home.reminisce.model.Session;
import com.home.reminisce.model.SessionStatus;
import com.home.reminisce.repository.SessionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final UserDetailsService userDetailsService;

    public SessionServiceImpl(SessionRepository sessionRepository, UserDetailsService userDetailsService) {
        this.sessionRepository = sessionRepository;
        this.userDetailsService = userDetailsService;
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
        Optional<Session> optionalSession = sessionRepository.findById(id);

        if (optionalSession.isPresent()) {
            Session session = optionalSession.get();
            if (!Objects.equals(session.getStatus(), SessionStatus.COMPLETED)) {
                session.setStatus(status);
                session.setEndedOn(Instant.now());
                return sessionRepository.save(session);
            } else {
                return session;
            }
        } else {
            throw new EntityNotFoundException("Session not found with ID: " + id);
        }
    }
}
