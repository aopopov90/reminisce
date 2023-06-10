package com.home.reminisce.api.controller;

import com.home.reminisce.api.model.ReactionRequest;
import com.home.reminisce.model.Reaction;
import com.home.reminisce.service.ReactionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        Reaction createdReaction = reactionService.createReaction(reactionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReaction(@PathVariable Long id) {
        reactionService.deleteReaction(id);
        return ResponseEntity.noContent().build();
    }
}
