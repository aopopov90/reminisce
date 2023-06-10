package com.home.reminisce.api.controller;

import com.home.reminisce.api.model.CommentRequest;
import com.home.reminisce.model.Comment;
import com.home.reminisce.service.CommentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
@SecurityRequirement(name = "bearerAuth")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<Comment> createComment(@RequestBody CommentRequest commentRequest) {
        Comment createdComment = commentService.createComment(commentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comment> updateComment(@RequestBody CommentRequest commentRequest, @PathVariable Long id) {
        Comment updatedComment = commentService.updateComment(commentRequest, id);
        return ResponseEntity.ok(updatedComment);
    }
}
