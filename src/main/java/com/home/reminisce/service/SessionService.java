package com.home.reminisce.service;

import com.home.reminisce.api.model.SessionRequest;
import com.home.reminisce.model.Session;
import com.home.reminisce.model.SessionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SessionService {

    Session findById(long l);

    List<Session> getAll();

    boolean sessionExists(long sessionId);

    Session createSession(SessionRequest sessionRequest);

    Session updateSessionStatus(long id, SessionStatus sessionStatus);

    Page<Session> getPaginatedSessions(Pageable pageable);
}
