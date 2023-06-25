package com.home.reminisce.service;

import com.home.reminisce.repository.ParticipationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParticipationServiceNoAuthTest {

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
    public void setup(TestInfo info) {
        MockitoAnnotations.openMocks(this);
        participationService = new ParticipationServiceImpl(sessionService, participationRepository);
    }

    @Test
    public void testAddParticipants_whenNoSessionFound_ShouldThrowNoSuchElementException() {
        lenient().when(sessionService.findById(anyLong())).thenReturn(null);

        assertThrows(NoSuchElementException.class, () ->
                participationService.addParticipations(1L, List.of("new_participant@example.com")));

        verify(sessionService, times(1)).findById(anyLong());
        verify(participationRepository, never()).findBySessionId(anyLong());
        verify(participationRepository, never()).saveAll(anyIterable());
    }

}