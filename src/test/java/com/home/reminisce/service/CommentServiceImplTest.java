package com.home.reminisce.service;

import com.home.reminisce.api.model.CommentRequest;
import com.home.reminisce.exceptions.UnauthorizedAccessException;
import com.home.reminisce.model.Comment;
import com.home.reminisce.model.Session;
import com.home.reminisce.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class CommentServiceImplTest {
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private SessionService sessionService;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    public void setup() {
        final String authenticatedUser = "user@example.com";
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        Session session = Session.builder()
                .participants(List.of(authenticatedUser))
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(authenticatedUser);
        when(sessionService.findById(anyLong())).thenReturn(session);

        commentRepository = Mockito.mock(CommentRepository.class);
        commentService = new CommentServiceImpl(commentRepository, sessionService);
    }

    @Test
    public void testCreateComment_WhenCalled_ShouldSaveComment() {
        // Arrange
        CommentRequest commentRequest = new CommentRequest(1L, "Sample comment", 1);
        Comment expectedComment = Comment.builder()
                .text("sample comment")
                .build();

        // Act
        when(commentRepository.save(any(Comment.class))).thenReturn(expectedComment);
        Comment createdComment = commentService.createComment(commentRequest);

        // Assert
        verify(commentRepository, times(1)).save(any(Comment.class));
        assert createdComment.equals(expectedComment);
    }

    @Test
    public void testDeleteComment_ValidCommentIdAndAuthorizedUser_ShouldDeleteComment() {
        // Arrange
        Long commentId = 1L;
        String authenticatedUser = "user@example.com";
        Comment comment = Comment.builder()
                .text("sample comment")
                .authoredBy(authenticatedUser)
                .build();

        // Act
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        commentService.deleteComment(commentId);

        // Assert
        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    public void testDeleteComment_ValidCommentIdButUnauthorizedUser_ShouldThrowUnauthorizedAccessException() {
        // Arrange
        Long commentId = 1L;
        String authoredBy = "otheruser@example.com";
        Comment comment = Comment.builder()
                .text("sample comment")
                .authoredBy(authoredBy)
                .build();

        // Act and Assert
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        assertThrows(UnauthorizedAccessException.class, () -> commentService.deleteComment(commentId));
        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, times(0)).delete(any(Comment.class));
    }

    @Test
    public void testDeleteComment_InvalidCommentId_ShouldThrowNoSuchElementException() {
        // Arrange
        Long commentId = 1L;

        // Act and Assert
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> commentService.deleteComment(commentId));
        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, times(0)).delete(any(Comment.class));
    }

    @Test
    public void testUpdateComment_ValidCommentIdAndAuthorizedUser_ShouldUpdateComment() {
        // Arrange
        Long commentId = 1L;
        String authenticatedUser = "user@example.com";
        CommentRequest commentRequest = new CommentRequest(1L, "Sample comment", 1);
        Comment comment = Comment.builder()
                .text("sample comment")
                .authoredBy(authenticatedUser)
                .build();
        Comment updatedComment = Comment.builder()
                .text("updated comment")
                .authoredBy(authenticatedUser)
                .build();

        // Act
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(updatedComment);
        Comment result = commentService.updateComment(commentRequest, commentId);

        // Assert
        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, times(1)).save(any(Comment.class));
        assert result.equals(updatedComment);
    }

    @Test
    public void testUpdateComment_ValidCommentIdButUnauthorizedUser_ShouldThrowUnauthorizedAccessException() {
        // Arrange
        Long commentId = 1L;
        String authoredBy = "otheruser@example.com";
        CommentRequest commentRequest = new CommentRequest(1L, "Sample comment", 1);
        Comment comment = Comment.builder()
                .text("sample comment")
                .authoredBy(authoredBy)
                .build();

        // Act and Assert
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        assertThrows(UnauthorizedAccessException.class, () -> commentService.updateComment(commentRequest, commentId));
        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, times(0)).save(any(Comment.class));
    }

    @Test
    public void testUpdateComment_InvalidCommentId_ShouldThrowNoSuchElementException() {
        // Arrange
        Long commentId = 1L;
        CommentRequest commentRequest = new CommentRequest(1L, "Sample comment", 1);

        // Act and Assert
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> commentService.updateComment(commentRequest, commentId));
        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, times(0)).save(any(Comment.class));
    }

    @Test
    public void testCreateComment_UserNotSessionParticipant_ShouldThrowUnauthorizedAccessException() {
        Session otherSession = Session.builder()
                .participants(List.of("otheruser@example.com"))
                .build();
        CommentRequest commentRequest = new CommentRequest(1L, "Sample comment", 1);

        when(sessionService.findById(anyLong())).thenReturn(otherSession);
        assertThrows(UnauthorizedAccessException.class, () -> commentService.createComment(commentRequest));
        verify(commentRepository, times(0)).save(any(Comment.class));
    }
}
