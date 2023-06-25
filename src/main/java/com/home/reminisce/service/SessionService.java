package com.home.reminisce.service;

import com.home.reminisce.api.model.SessionRequest;
import com.home.reminisce.model.Session;
import com.home.reminisce.model.SessionStatus;

import java.util.List;

public interface SessionService {

    Session findById(long l);

    List<Session> getAll();

    boolean sessionExists(long sessionId);

    Session createSession(SessionRequest sessionRequest);

    Session updateSessionStatus(long id, SessionStatus sessionStatus);

    List<String> addParticipants(Long sessionId, List<String> participants);
}
