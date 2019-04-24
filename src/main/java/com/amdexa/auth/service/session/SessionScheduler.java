package com.amdexa.auth.service.session;

import com.amdexa.auth.service.session.model.Session;
import com.amdexa.auth.service.session.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SessionScheduler {

    @Autowired
    private SessionRepository sessionRepository;

    @Scheduled(cron = "*/2 * * * *")
    public void sessionCleanup() {
        List<Session> expirySessions = new ArrayList<>();
        sessionRepository.findAll().forEach(s -> {
            if (s.getExpiryTime() < System.currentTimeMillis()) {
                expirySessions.add(s);
            }
        });
        if(!expirySessions.isEmpty()){
            sessionRepository.deleteAll(expirySessions);
        }
    }
}
