package com.home.reminisce.service;

import com.home.reminisce.exceptions.UnauthorizedAccessException;
import com.home.reminisce.model.Session;
import com.home.reminisce.model.SessionStatus;
import com.home.reminisce.repository.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceImplTest {

    @Mock
    SessionRepository sessionRepository;

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

        sessionService = new SessionServiceImpl(sessionRepository);
    }


    @Test
    public void testAddParticipants_whenUserIsSessionCreator_thenParticipantsAdded() {
        Session session = Session.builder().createdBy(authenticatedUser).build();

        when(sessionRepository.findById(anyLong())).thenReturn(Optional.of(session));
        List<String> participants = sessionService.addParticipants(1L, List.of("new_participant@example.com"));

        assertTrue(participants.contains("new_participant@example.com"));
        verify(sessionRepository, times(1)).save(any(Session.class));
    }

    @Test
    public void testAddParticipants_whenUserIsSessionParticipant_thenParticipantsAdded() {
        Session session = Session.builder()
                .createdBy("another_user@example.com")
                .participants(List.of(authenticatedUser))
                .build();

        when(sessionRepository.findById(anyLong())).thenReturn(Optional.of(session));
        List<String> participants = sessionService.addParticipants(1L, List.of("new_participant@example.com"));

        assertTrue(participants.contains("new_participant@example.com"));
        verify(sessionRepository, times(1)).save(any(Session.class));
    }

    @Test
    public void testAddParticipants_whenUserNotSessionParticipantNorCreator_ShouldThrowUnauthorizedAccessException() {
        Session session = Session.builder()
                .createdBy("another_user_1@example.com")
                .participants(List.of("another_user_2@example.com"))
                .build();

        when(sessionRepository.findById(anyLong())).thenReturn(Optional.of(session));

        assertThrows(UnauthorizedAccessException.class, () ->
                sessionService.addParticipants(1L, List.of("new_participant@example.com")));
        verify(sessionRepository, never()).save(any(Session.class));
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

//    @Test
//    public void testGetSessions_whenUserIsEitherParticipantOrCreator_thenOnlySuchSessionsDisplayed() {}
}