import com.home.reminisce.api.controller.ParticipationController;
import com.home.reminisce.exceptions.UnauthorizedAccessException;
import com.home.reminisce.model.Participation;
import com.home.reminisce.service.ParticipationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ParticipationControllerTest {

    @Mock
    private ParticipationService participationService;

    @InjectMocks
    private ParticipationController participationController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddParticipationsWithValidInput() {
        // Mock data
        Long sessionId = 1L;
        List<String> participants = new ArrayList<>();
        participants.add("John");
        participants.add("Jane");
        List<Participation> addedParticipations = new ArrayList<>();
        addedParticipations.add(Participation.builder().participantName("John").build());
        addedParticipations.add(Participation.builder().participantName("Jane").build());

        // Mock service method
        when(participationService.addParticipations(sessionId, participants)).thenReturn(addedParticipations);

        // Call the controller method
        ResponseEntity<?> response = participationController.addParticipations(sessionId, participants);

        // Verify the response
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(addedParticipations, response.getBody());

        // Verify the service method was called
        verify(participationService, times(1)).addParticipations(sessionId, participants);
        verifyNoMoreInteractions(participationService);
    }

    @Test
    public void testAddParticipationsWithUnauthorizedAccess() {
        // Mock data
        Long sessionId = 1L;
        List<String> participants = new ArrayList<>();
        participants.add("John");
        participants.add("Jane");
        UnauthorizedAccessException exception = new UnauthorizedAccessException("Unauthorized access");

        // Mock service method
        when(participationService.addParticipations(sessionId, participants)).thenThrow(exception);

        // Call the controller method
        ResponseEntity<?> response = participationController.addParticipations(sessionId, participants);

        // Verify the response
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Unauthorized access", response.getBody());

        // Verify the service method was called
        verify(participationService, times(1)).addParticipations(sessionId, participants);
        verifyNoMoreInteractions(participationService);
    }

    @Test
    public void testGetParticipationsWithValidInput() {
        // Mock data
        Long sessionId = 1L;
        List<Participation> participations = new ArrayList<>();
        participations.add(Participation.builder().participantName("John").build());
        participations.add(Participation.builder().participantName("Jane").build());

        // Mock service method
        when(participationService.getParticipations(sessionId)).thenReturn(participations);

        // Call the controller method
        ResponseEntity<List<Participation>> response = participationController.getParticipations(sessionId);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(participations, response.getBody());

        // Verify the service method was called
        verify(participationService, times(1)).getParticipations(sessionId);
        verifyNoMoreInteractions(participationService);
    }

    @Test
    public void testDeleteParticipationsWithValidInput() {
        // Mock data
        Long sessionId = 1L;
        List<String> participants = new ArrayList<>();
        participants.add("John");
        participants.add("Jane");

        // Call the controller method
        ResponseEntity<?> response = participationController.deleteParticipations(sessionId, participants);

        // Verify the response
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        // Verify the service method was called
        verify(participationService, times(1)).deleteParticipations(sessionId, participants);
        verifyNoMoreInteractions(participationService);
    }

    @Test
    public void testDeleteParticipationsWithUnauthorizedAccess() {
        // Mock data
        Long sessionId = 1L;
        List<String> participants = new ArrayList<>();
        participants.add("John");
        participants.add("Jane");
        UnauthorizedAccessException exception = new UnauthorizedAccessException("Unauthorized access");

        // Mock service method
        doThrow(exception).when(participationService).deleteParticipations(sessionId, participants);

        // Call the controller method
        ResponseEntity<?> response = participationController.deleteParticipations(sessionId, participants);

        // Verify the response
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Unauthorized access", response.getBody());

        // Verify the service method was called
        verify(participationService, times(1)).deleteParticipations(sessionId, participants);
        verifyNoMoreInteractions(participationService);
    }

}
