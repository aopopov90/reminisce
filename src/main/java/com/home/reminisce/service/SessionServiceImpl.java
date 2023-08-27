package com.home.reminisce.service;

import com.home.reminisce.api.model.SessionRequest;
import com.home.reminisce.exceptions.UnauthorizedAccessException;
import com.home.reminisce.model.Comment;
import com.home.reminisce.model.Participation;
import com.home.reminisce.model.Session;
import com.home.reminisce.model.SessionStatus;
import com.home.reminisce.repository.ParticipationRepository;
import com.home.reminisce.repository.SessionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;

    private final ParticipationRepository participationRepository;

    public SessionServiceImpl(SessionRepository sessionRepository, ParticipationRepository participationRepository) {
        this.sessionRepository = sessionRepository;
        this.participationRepository = participationRepository;
    }

    public Session findById(long id) {
        Optional<Session> optionalSession = sessionRepository.findById(id);

        if (!isAuthorizedToEditSession(optionalSession.get())) {
            throw new UnauthorizedAccessException("You are not authorized to view this session.");
        }

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
        String authenticatedUser = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Session> sessionsCreatorOf = sessionRepository.findByCreatedBy(authenticatedUser);
        List<Participation> participations = participationRepository.findByParticipantName(authenticatedUser);
        List<Session> sessionsParticipantOf = sessionRepository.findAllById(
                participations.stream().map(participation -> participation.getSessionId()).collect(Collectors.toList())
        );
        return Stream.concat(sessionsCreatorOf.stream(), sessionsParticipantOf.stream()).collect(Collectors.toList());
    }

    public Session createSession(SessionRequest sessionRequest) {
        return sessionRepository.save(Session.builder()
                .name(sessionRequest.name())
                .createdBy(SecurityContextHolder.getContext().getAuthentication().getName())
                .status(SessionStatus.IN_PROGRESS)
                .createdOn(Instant.now())
                .comments(List.of(Comment.builder().createdOn(Instant.now()).authoredBy("reminisce").text("Enjoy your retro").build()))
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
    public void deleteSession(Long id) {
        Optional<Session> sessionOptional = sessionRepository.findById(id);
        if (sessionOptional.isPresent()) {
            Session session = sessionOptional.get();
            if (session.getCreatedBy().equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
                sessionRepository.delete(sessionOptional.get());
            } else {
                throw new UnauthorizedAccessException("You are not authorized to delete this session.");
            }
        } else {
            throw new NoSuchElementException("Session not found with ID" + id);
        }
    }

    @Override
    public Page<Session> getPaginatedSessions(Pageable pageable) {
        return sessionRepository.findAll(pageable);
    }

    private boolean isAuthorizedToEditSession(Session session) {
        String authenticatedUser = SecurityContextHolder.getContext().getAuthentication().getName();
        return Optional.ofNullable(session.getCreatedBy()).orElse("").equals(authenticatedUser)
                || participationRepository.findBySessionIdAndParticipantName(session.getId(), authenticatedUser).isPresent();
    }

    private boolean isAuthorizedToViewSession(Session session) {
        String authenticatedUser = SecurityContextHolder.getContext().getAuthentication().getName();
        return Optional.ofNullable(session.getCreatedBy()).orElse("").equals(authenticatedUser)
                || participationRepository.findBySessionIdAndParticipantName(session.getId(), authenticatedUser).isPresent();
    }

}
