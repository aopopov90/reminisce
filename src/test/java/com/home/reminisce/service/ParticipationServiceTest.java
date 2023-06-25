package com.home.reminisce.service;

import com.home.reminisce.exceptions.UnauthorizedAccessException;
import com.home.reminisce.model.Participation;
import com.home.reminisce.model.Session;
import com.home.reminisce.repository.ParticipationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParticipationServiceTest {

    @Mock
    private ParticipationRepository participationRepository;

    @Mock
    private SessionService sessionService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    final String authenticatedUser = "user@example.com";

    private ParticipationService participationService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(authenticatedUser);

        participationService = new ParticipationServiceImpl(sessionService, participationRepository);
    }

    @Test
    public void testAddParticipants_whenUserIsSessionCreator_thenParticipantsAdded() {
        Session session = Session.builder().createdBy(authenticatedUser).build();

        when(sessionService.findById(anyLong())).thenReturn(session);
        participationService.addParticipations(1L, List.of("new_participant@example.com"));

        verify(sessionService, times(1)).findById(anyLong());
        verify(participationRepository, never()).findBySessionId(anyIterable());
        verify(participationRepository, times(1)).saveAll(anyIterable());
    }

    @Test
    public void testAddParticipants_whenUserIsSessionParticipant_thenParticipantsAdded() {
        Session session = new Session();
        Participation participation = Participation.builder().participantName(authenticatedUser).build();

        when(sessionService.findById(anyLong())).thenReturn(session);
        when(participationRepository.findBySessionId(anyIterable())).thenReturn(List.of(participation));
        participationService.addParticipations(1L, List.of("new_participant@example.com"));

        verify(sessionService, times(1)).findById(anyLong());
        verify(participationRepository, times(1)).findBySessionId(anyIterable());
        verify(participationRepository, times(1)).saveAll(anyIterable());
    }

    @Test
    public void testAddParticipants_whenUserNotSessionParticipantNorCreator_ShouldThrowUnauthorizedAccessException() {
        Session session = Session.builder().createdBy("another_user@example.com").build();
        Participation participation = Participation.builder().participantName("another_user@example.com").build();

        when(sessionService.findById(anyLong())).thenReturn(session);
        when(participationRepository.findBySessionId(anyIterable())).thenReturn(List.of(participation));

        assertThrows(UnauthorizedAccessException.class, () ->
                participationService.addParticipations(1L, List.of("new_participant@example.com")));

        verify(sessionService, times(1)).findById(anyLong());
        verify(participationRepository, times(1)).findBySessionId(anyIterable());
        verify(participationRepository, never()).saveAll(anyIterable());
    }

    @Test
    public void testDeleteParticipants_whenUserIsSessionCreator_thenParticipantsDeleted() {
        Session session = Session.builder().createdBy(authenticatedUser).build();

        when(sessionService.findById(anyLong())).thenReturn(session);
        participationService.deleteParticipations(1L, List.of("participant1@example.com"));

        verify(sessionService, times(1)).findById(anyLong());
        verify(participationRepository, never()).findBySessionId(anyIterable());
        verify(participationRepository, times(1))
                .deleteBySessionIdAndParticipantNameIn(1L, List.of("participant1@example.com"));
    }

    @Test
    public void testDeleteParticipants_whenUserIsSessionParticipant_thenParticipantsDeleted() {
        Session session = new Session();
        Participation participation = Participation.builder().participantName(authenticatedUser).build();

        when(sessionService.findById(anyLong())).thenReturn(session);
        when(participationRepository.findBySessionId(anyIterable())).thenReturn(List.of(participation));
        participationService.deleteParticipations(1L, List.of("participant1@example.com"));

        verify(sessionService, times(1)).findById(anyLong());
        verify(participationRepository, times(1)).findBySessionId(anyIterable());
        verify(participationRepository, times(1))
                .deleteBySessionIdAndParticipantNameIn(1L, List.of("participant1@example.com"));
    }

    @Test
    public void testDeleteParticipants_whenUserNotSessionParticipantNorCreator_ShouldThrowUnauthorizedAccessException() {
        Session session = Session.builder().createdBy("another_user@example.com").build();
        Participation participation = Participation.builder().participantName("another_user@example.com").build();

        when(sessionService.findById(anyLong())).thenReturn(session);
        when(participationRepository.findBySessionId(anyIterable())).thenReturn(List.of(participation));

        assertThrows(UnauthorizedAccessException.class, () ->
                participationService.deleteParticipations(1L, List.of("participant1@example.com")));

        verify(sessionService, times(1)).findById(anyLong());
        verify(participationRepository, times(1)).findBySessionId(anyIterable());
        verify(participationRepository, never())
                .deleteBySessionIdAndParticipantNameIn(1L, List.of("participant1@example.com"));
    }
}