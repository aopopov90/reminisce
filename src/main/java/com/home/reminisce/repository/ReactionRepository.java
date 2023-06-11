package com.home.reminisce.repository;

import com.home.reminisce.model.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    List<Reaction> findByCommentIdAndAuthoredBy(Long commentId, String authoredBy);
}
