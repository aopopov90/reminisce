package com.home.reminisce.api.controller;

import com.home.reminisce.api.model.SessionRequest;
import com.home.reminisce.exceptions.UnauthorizedAccessException;
import com.home.reminisce.model.Session;
import com.home.reminisce.model.SessionStatus;
import com.home.reminisce.service.SessionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@SecurityRequirement(name = "bearerAuth")
public class SessionController {

    private SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("/sessions/{id}")
    public ResponseEntity<?> getSessionById(@PathVariable long id) throws Exception {
        try {
            return ResponseEntity.ok(sessionService.findById(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/sessions")
    public List<Session> getSessions() throws Exception {
        return sessionService.getAll();
    }

    @PostMapping(value = "/sessions", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Session createSession(@RequestBody SessionRequest sessionRequest) {
        return sessionService.createSession(sessionRequest);
    }

    @PatchMapping("/sessions/{id}/end")
    public ResponseEntity<Session> endSession(@PathVariable long id) {
        try {
            return ResponseEntity.ok(sessionService.updateSessionStatus(id, SessionStatus.COMPLETED));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/sessions_p")
    public Page<Session> getSessions(Pageable pageable) throws Exception {
        return sessionService.getPaginatedSessions(pageable);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSession(@PathVariable Long id) {
        try {
            sessionService.deleteSession(id);
            return ResponseEntity.ok("Session deleted successfully.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}
