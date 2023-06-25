package com.home.reminisce.repository;

import com.home.reminisce.model.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    List<Participation> findBySessionId(Long id);

    void deleteBySessionIdAndParticipantNameIn(Long sessionId, List<String> participantNames);
}
