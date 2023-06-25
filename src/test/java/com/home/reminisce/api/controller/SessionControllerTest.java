package com.home.reminisce.api.controller;

import com.home.reminisce.api.model.SessionRequest;
import com.home.reminisce.model.Session;
import com.home.reminisce.model.SessionStatus;
import com.home.reminisce.service.SessionService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionControllerTest {

    @Mock
    private SessionService sessionService;

    @InjectMocks
    private SessionController sessionController;

    @Test
    void givenValidSessionId_whenGetSessionById_thenReturnSession() throws Exception {
        // Arrange
        long sessionId = 1L;
        Session session = Session.builder().id(sessionId).build();
        when(sessionService.findById(sessionId)).thenReturn(session);

        // Act
        ResponseEntity<?> response = sessionController.getSessionById(sessionId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(session, response.getBody());
        verify(sessionService).findById(sessionId);
    }

    @Test
    void givenInvalidSessionId_whenGetSessionById_thenReturnNotFound() throws Exception {
        // Arrange
        long sessionId = 1L;
        when(sessionService.findById(sessionId)).thenThrow(EntityNotFoundException.class);

        // Act
        ResponseEntity<?> response = sessionController.getSessionById(sessionId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(sessionService).findById(sessionId);
    }

    @Test
    void getSessions_thenReturnListOfSessions() throws Exception {
        // Arrange
        List<Session> sessions = Arrays.asList(
                Session.builder().id(1L).build(),
                Session.builder().id(2L).build()
        );
        when(sessionService.getAll()).thenReturn(sessions);

        // Act
        List<Session> response = sessionController.getSessions();

        // Assert
        assertEquals(sessions, response);
        verify(sessionService).getAll();
    }

    @Test
    void givenValidSessionRequest_whenCreateSession_thenReturnCreatedSession() {
        // Arrange
        SessionRequest sessionRequest = new SessionRequest("test");
        Session createdSession = Session.builder().id(1L).build();
        when(sessionService.createSession(sessionRequest)).thenReturn(createdSession);

        // Act
        Session response = sessionController.createSession(sessionRequest);

        // Assert
        assertEquals(createdSession, response);
        verify(sessionService).createSession(sessionRequest);
    }

    @Test
    void givenValidSessionId_whenEndSession_thenReturnUpdatedSession() throws EntityNotFoundException {
        // Arrange
        long sessionId = 1L;
        Session updatedSession = Session.builder().id(sessionId).status(SessionStatus.COMPLETED).build();
        when(sessionService.updateSessionStatus(sessionId, SessionStatus.COMPLETED)).thenReturn(updatedSession);

        // Act
        ResponseEntity<Session> response = sessionController.endSession(sessionId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedSession, response.getBody());
        verify(sessionService).updateSessionStatus(sessionId, SessionStatus.COMPLETED);
    }

    @Test
    void givenInvalidSessionId_whenEndSession_thenReturnNotFound() throws EntityNotFoundException {
        // Arrange
        long sessionId = 1L;
        when(sessionService.updateSessionStatus(sessionId, SessionStatus.COMPLETED)).thenThrow(EntityNotFoundException.class);

        // Act
        ResponseEntity<Session> response = sessionController.endSession(sessionId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(sessionService).updateSessionStatus(sessionId, SessionStatus.COMPLETED);
    }
}
