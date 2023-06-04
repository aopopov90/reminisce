package com.home.reminisce.service;

import com.home.reminisce.api.model.SessionRequest;
import com.home.reminisce.model.Session;
import com.home.reminisce.model.SessionStatus;

import java.util.List;

public interface SessionService {

    Session findById(long l);

    List<Session> getAll();

    Session createSession(SessionRequest sessionRequest);

    Session updateSessionStatus(long id, SessionStatus sessionStatus);
}
