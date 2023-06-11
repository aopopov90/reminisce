package com.home.reminisce.api.controller;

import com.home.reminisce.api.model.ReactionRequest;
import com.home.reminisce.exceptions.UnauthorizedAccessException;
import com.home.reminisce.model.Reaction;
import com.home.reminisce.service.ReactionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/reactions")
@SecurityRequirement(name = "bearerAuth")
public class ReactionController {

    private final ReactionService reactionService;

    public ReactionController(ReactionService reactionService) {
        this.reactionService = reactionService;
    }

    @PostMapping
    public ResponseEntity<Reaction> createReaction(@RequestBody ReactionRequest reactionRequest) {
        return reactionService.createReaction(reactionRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReaction(@PathVariable Long id) {
        try {
            reactionService.deleteReaction(id);
            return ResponseEntity.ok("Reaction deleted successfully.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reaction not found with ID " + id);
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}
