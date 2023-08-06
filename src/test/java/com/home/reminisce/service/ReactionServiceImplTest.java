package com.home.reminisce.service;

import com.home.reminisce.api.model.ReactionRequest;
import com.home.reminisce.exceptions.UnauthorizedAccessException;
import com.home.reminisce.model.Reaction;
import com.home.reminisce.model.ReactionType;
import com.home.reminisce.repository.ReactionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ReactionServiceImplTest {
    @Mock
    private ReactionRepository reactionRepository;

    private ReactionServiceImpl reactionService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user@example.com");

        reactionRepository = Mockito.mock(ReactionRepository.class);
        reactionService = new ReactionServiceImpl(reactionRepository, messagingTemplate);
    }

    @Test
    public void testCreateReaction_WhenReactionDoesNotExist_ShouldCreateNewReaction() {
        // Arrange
        String userEmail = "user@example.com";
        Long commentId = 1L;
        ReactionRequest reactionRequest = new ReactionRequest(commentId, ReactionType.LIKE);

        // Mocking ReactionRepository
        List<Reaction> emptyReactionsList = new ArrayList<>();
        Mockito.when(reactionRepository.findByCommentIdAndAuthoredBy(commentId, userEmail))
                .thenReturn(emptyReactionsList);
        Mockito.when(reactionRepository.save(any(Reaction.class)))
                .thenReturn(Reaction.builder()
                        .createdOn(Instant.now())
                        .reactionType(reactionRequest.reactionType())
                        .authoredBy(userEmail)
                        .commentId(commentId)
                        .build());

        // Act
        ResponseEntity<Reaction> response = reactionService.createReaction(reactionRequest);

        // Assert
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(reactionRequest.reactionType(), response.getBody().getReactionType());

        verify(reactionRepository, times(1)).save(any(Reaction.class));
        verify(reactionRepository, never()).delete(any(Reaction.class));
    }

    @Test
    public void testCreateReaction_WhenReactionExistsWithSameType_ShouldNotModifyReaction() {
        // Arrange
        String userEmail = "user@example.com";
        Long commentId = 1L;
        ReactionRequest reactionRequest = new ReactionRequest(commentId, ReactionType.LIKE);

        // Mocking ReactionRepository
        List<Reaction> reactionsList = new ArrayList<>();
        reactionsList.add(Reaction.builder()
                .createdOn(Instant.now())
                .reactionType(ReactionType.LIKE)
                .authoredBy(userEmail)
                .commentId(commentId)
                .build());
        Mockito.when(reactionRepository.findByCommentIdAndAuthoredBy(commentId, userEmail))
                .thenReturn(reactionsList);

        // Act
        ResponseEntity<Reaction> response = reactionService.createReaction(reactionRequest);

        // Assert
        Assertions.assertEquals(HttpStatus.NOT_MODIFIED, response.getStatusCode());
        Assertions.assertNull(response.getBody());

        verify(reactionRepository, never()).save(any(Reaction.class));
        verify(reactionRepository, never()).delete(any(Reaction.class));
    }

    @Test
    public void testCreateReaction_WhenReactionExistsWithDifferentType_ShouldUpdateReaction() {
        // Arrange
        String userEmail = "user@example.com";
        Long commentId = 1L;
        ReactionRequest reactionRequest = new ReactionRequest(commentId, ReactionType.LIKE);

        // Mocking ReactionRepository
        List<Reaction> reactionsList = new ArrayList<>();
        reactionsList.add(Reaction.builder()
                .createdOn(Instant.now())
                .reactionType(ReactionType.DISLIKE)
                .authoredBy(userEmail)
                .commentId(commentId)
                .build());
        when(reactionRepository.findByCommentIdAndAuthoredBy(commentId, userEmail))
                .thenReturn(reactionsList);
        when(reactionRepository.save(any(Reaction.class)))
                .thenReturn(Reaction.builder()
                        .createdOn(Instant.now())
                        .reactionType(reactionRequest.reactionType())
                        .authoredBy(userEmail)
                        .commentId(commentId)
                        .build());

        // Act
        ResponseEntity<Reaction> response = reactionService.createReaction(reactionRequest);

        // Assert
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(reactionRequest.reactionType(), response.getBody().getReactionType());

        verify(reactionRepository, times(1)).save(any(Reaction.class));
        verify(reactionRepository, times(1)).delete(any(Reaction.class));
    }

    @Test
    public void testDeleteReaction_WhenReactionDoesNotExist_ShouldThrowNoSuchElementException() {
        // Arrange
        Long reactionId = 1L;

        // Mocking ReactionRepository
        Optional<Reaction> reactionOptional = Optional.empty();
        Mockito.when(reactionRepository.findById(reactionId))
                .thenReturn(reactionOptional);

        // Act & Assert
        Assertions.assertThrows(NoSuchElementException.class,
                () -> reactionService.deleteReaction(reactionId),
                "Reaction not found with ID " + reactionId);

        verify(reactionRepository, times(1)).findById(reactionId);
        verify(reactionRepository, never()).delete(any(Reaction.class));
    }

    @Test
    public void testDeleteReaction_WhenReactionAuthoredByAnotherUser_ShouldThrowUnauthorizedAccessException() {
        // Arrange
        String userEmail = "another.user@example.com";
        Long reactionId = 1L;

        // Mocking ReactionRepository
        Reaction existingReaction = Reaction.builder()
                .reactionType(ReactionType.DISLIKE)
                .authoredBy(userEmail)
                .build();

        when(reactionRepository.findById(anyLong())).thenReturn(Optional.of(existingReaction));

        // Act & Assert
        Assertions.assertThrows(UnauthorizedAccessException.class,
                () -> reactionService.deleteReaction(1L),
                "You are not authorized to delete this reaction.");
        verify(reactionRepository, times(1)).findById(anyLong());
        verify(reactionRepository, never()).delete(any(Reaction.class));
    }

    @Test
    public void testDeleteReaction_WhenReactionAuthoredByTheAuthenticatedUser_ShouldDeleteReaction() {
        // Arrange
        String userEmail = "user@example.com";
        Long reactionId = 1L;

        // Mocking ReactionRepository
        Reaction existingReaction = Reaction.builder()
                .reactionType(ReactionType.DISLIKE)
                .authoredBy(userEmail)
                .build();

        when(reactionRepository.findById(anyLong())).thenReturn(Optional.of(existingReaction));

        // Act
        reactionService.deleteReaction(reactionId);

        // Assert
        verify(reactionRepository, times(1)).findById(reactionId);
        verify(reactionRepository, times(1)).delete(any(Reaction.class));
    }
}

