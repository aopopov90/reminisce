package com.home.reminisce.service;

import com.home.reminisce.model.Session;
import com.home.reminisce.model.SessionStatus;
import com.home.reminisce.repository.SessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceImplTest {

    @Mock
    SessionRepository sessionRepository;

    @InjectMocks
    private SessionServiceImpl sessionService;

    @Test
    public void givenInProgressSession_whenUpdatingToCompletedStatus_thenStatusIsUpdatedAndSaved() {
        Session session = Session.builder().id(1L).status(SessionStatus.IN_PROGRESS).build();

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        sessionService.updateSessionStatus(1L, SessionStatus.COMPLETED);

        assertEquals(SessionStatus.COMPLETED, session.getStatus());
        verify(sessionRepository).save(session);
    }

    @Test
    public void givenCompletedSession_whenUpdatingStatus_thenTimestampRemainsUnchanged() {
        Instant endedOn = Instant.now();
        Session session = Session.builder().id(1L).status(SessionStatus.COMPLETED).endedOn(endedOn).build();

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        sessionService.updateSessionStatus(1L, SessionStatus.COMPLETED);

        assertEquals(endedOn, session.getEndedOn());
        verify(sessionRepository, never()).save(session);
    }
}