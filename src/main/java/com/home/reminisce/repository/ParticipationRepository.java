package com.home.reminisce.repository;

import com.home.reminisce.model.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    List<Participation> findBySessionId(Iterable<Long> ids);

    List<Participation> findBySessionIdAndParticipantNameIn(Long sessionId, List<String> participantNames);

    void deleteBySessionIdAndParticipantNameIn(Long sessionId, List<String> participantNames);
}
