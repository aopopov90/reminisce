package com.home.reminisce.repository;

import com.home.reminisce.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long>, PagingAndSortingRepository<Session, Long> {
    List<Session> findByCreatedBy(String createdBy);
}
