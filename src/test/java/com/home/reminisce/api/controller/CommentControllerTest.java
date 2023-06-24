package com.home.reminisce.api.controller;

import com.home.reminisce.api.model.CommentRequest;
import com.home.reminisce.exceptions.UnauthorizedAccessException;
import com.home.reminisce.model.Comment;
import com.home.reminisce.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class CommentControllerTest {
    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateComment_ValidCommentRequest_ShouldReturnCreatedStatusAndComment() {
        // Arrange
        CommentRequest commentRequest = new CommentRequest(1L, "Sample comment", 1);
        Comment createdComment = Comment.builder()
                .text("sample comment")
                .build();

        // Act
        when(commentService.createComment(any(CommentRequest.class))).thenReturn(createdComment);
        ResponseEntity<?> response = commentController.createComment(commentRequest);

        // Assert
        verify(commentService, times(1)).createComment(any(CommentRequest.class));
        assert response.getStatusCode() == HttpStatus.CREATED;
        assert response.getBody() == createdComment;
    }

    @Test
    public void testDeleteComment_ValidCommentId_ShouldReturnOkStatusAndSuccessMessage() {
        // Arrange
        Long commentId = 1L;

        // Act
        ResponseEntity<String> response = commentController.deleteComment(commentId);

        // Assert
        verify(commentService, times(1)).deleteComment(commentId);
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody().equals("Comment deleted successfully.");
    }

    @Test
    public void testDeleteComment_InvalidCommentId_ShouldReturnNotFoundStatusAndErrorMessage() {
        // Arrange
        Long commentId = 1L;
        String errorMessage = "Comment not found with ID" + commentId;

        // Act
        doThrow(new NoSuchElementException(errorMessage)).when(commentService).deleteComment(commentId);
        ResponseEntity<String> response = commentController.deleteComment(commentId);

        // Assert
        verify(commentService, times(1)).deleteComment(commentId);
        assert response.getStatusCode() == HttpStatus.NOT_FOUND;
        assert response.getBody().equals(errorMessage);
    }

    @Test
    public void testDeleteComment_UnauthorizedAccessException_ShouldReturnForbiddenStatusAndErrorMessage() {
        // Arrange
        Long commentId = 1L;
        String errorMessage = "You are not authorized to delete this comment.";

        // Act
        doThrow(new UnauthorizedAccessException(errorMessage)).when(commentService).deleteComment(commentId);
        ResponseEntity<String> response = commentController.deleteComment(commentId);

        // Assert
        verify(commentService, times(1)).deleteComment(commentId);
        assert response.getStatusCode() == HttpStatus.FORBIDDEN;
        assert response.getBody().equals(errorMessage);
    }

    @Test
    public void testUpdateComment_ValidCommentRequestAndCommentId_ShouldReturnOkStatusAndUpdatedComment() {
        // Arrange
        Long commentId = 1L;
        CommentRequest commentRequest = new CommentRequest(1L, "Updated comment", 1);
        Comment updatedComment = Comment.builder()
                .text("updated comment")
                .build();

        // Act
        when(commentService.updateComment(any(CommentRequest.class), anyLong())).thenReturn(updatedComment);
        ResponseEntity<Comment> response = commentController.updateComment(commentRequest, commentId);

        // Assert
        verify(commentService, times(1)).updateComment(any(CommentRequest.class), anyLong());
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody() == updatedComment;
    }

    @Test
    public void testCreateComment_UserNotSessionParticipant_ShouldReturnForbiddenStatusAndErrorMessage() {
        // Arrange
        String errorMessage = "You are not authorized to comment in this session.";
        CommentRequest commentRequest = new CommentRequest(1L, "Sample comment", 1);

        // Act
        doThrow(new UnauthorizedAccessException(errorMessage)).when(commentService).createComment(commentRequest);
        ResponseEntity<?> response = commentController.createComment(commentRequest);

        // Assert
        verify(commentService, times(1)).createComment(commentRequest);
        assert response.getStatusCode() == HttpStatus.FORBIDDEN;
        assert response.getBody().equals(errorMessage);
    }
}
