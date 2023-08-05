package com.home.reminisce.api.controller;

import com.home.reminisce.api.model.CommentRequest;
import com.home.reminisce.exceptions.UnauthorizedAccessException;
import com.home.reminisce.model.Comment;
import com.home.reminisce.service.CommentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/comments")
@SecurityRequirement(name = "bearerAuth")
public class CommentController {

    private final CommentService commentService;
    private final SimpMessagingTemplate messagingTemplate;

    public CommentController(CommentService commentService, SimpMessagingTemplate messagingTemplate) {
        this.commentService = commentService;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody CommentRequest commentRequest) {
        try {
            Comment createdComment = commentService.createComment(commentRequest);
            messagingTemplate.convertAndSend("/topic/newComment", createdComment);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

//    @SendTo("/topic/newComment")
//    public Comment broadcastComment(@Payload Comment comment) {
//        return comment;
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long id) {
        try {
            commentService.deleteComment(id);
            return ResponseEntity.ok("Comment deleted successfully.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comment> updateComment(@RequestBody CommentRequest commentRequest, @PathVariable Long id) {
        Comment updatedComment = commentService.updateComment(commentRequest, id);
        return ResponseEntity.ok(updatedComment);
    }
}
