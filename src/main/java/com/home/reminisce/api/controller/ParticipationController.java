package com.home.reminisce.api.controller;

import com.home.reminisce.exceptions.UnauthorizedAccessException;
import com.home.reminisce.model.Participation;
import com.home.reminisce.service.ParticipationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/participations")
@SecurityRequirement(name = "bearerAuth")
public class ParticipationController {

    private final ParticipationService participationService;

    public ParticipationController(ParticipationService participationService) {
        this.participationService = participationService;
    }

    @PostMapping("/{sessionId}/add")
    public ResponseEntity<?> addParticipations(
            @PathVariable("sessionId") Long sessionId,
            @RequestBody List<String> participants) {
        try {
            List<Participation> addedParticipations = participationService.addParticipations(sessionId, participants);
            return ResponseEntity.status(HttpStatus.CREATED).body(addedParticipations);
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<List<Participation>> getParticipations(@PathVariable("sessionId") Long sessionId) {
        return ResponseEntity.ok(participationService.getParticipations(sessionId));
    }

    @DeleteMapping("/{sessionId}/delete")
    public ResponseEntity<?> deleteParticipations(
            @PathVariable("sessionId") Long sessionId,
            @RequestBody List<String> participants) {
        try {
            participationService.deleteParticipations(sessionId, participants);
            return ResponseEntity.noContent().build();
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
