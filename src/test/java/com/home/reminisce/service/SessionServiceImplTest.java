package com.home.reminisce.service;

import com.home.reminisce.model.Session;
import com.home.reminisce.model.SessionStatus;
import com.home.reminisce.repository.ParticipationRepository;
import com.home.reminisce.repository.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceImplTest {

    @Mock
    SessionRepository sessionRepository;

    @Mock
    private ParticipationService participationService;

    @Mock
    private ParticipationRepository participationRepository;

    @InjectMocks
    private SessionServiceImpl sessionService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    final String authenticatedUser = "user@example.com";

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(authenticatedUser);

        sessionService = new SessionServiceImpl(sessionRepository, participationRepository);
    }

    @Test
    public void givenInProgressSession_whenUpdatingToCompletedStatus_thenStatusIsUpdatedAndSaved() {
        Session session = Session.builder().id(1L).createdBy(authenticatedUser).status(SessionStatus.IN_PROGRESS).build();

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        sessionService.updateSessionStatus(1L, SessionStatus.COMPLETED);

        assertEquals(SessionStatus.COMPLETED, session.getStatus());
        verify(sessionRepository).save(session);
    }

    @Test
    public void givenCompletedSession_whenUpdatingStatus_thenTimestampRemainsUnchanged() {
        Instant endedOn = Instant.now();
        Session session = Session.builder().id(1L).createdBy(authenticatedUser).status(SessionStatus.COMPLETED).endedOn(endedOn).build();

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        sessionService.updateSessionStatus(1L, SessionStatus.COMPLETED);

        assertEquals(endedOn, session.getEndedOn());
        verify(sessionRepository, never()).save(session);
    }

}