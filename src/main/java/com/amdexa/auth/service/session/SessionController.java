package com.amdexa.auth.service.session;

import com.amdexa.auth.service.session.model.Session;
import com.amdexa.auth.service.session.repository.SessionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping(path = "/auth/session")
public class SessionController {

    @Autowired
    private SessionRepository sessionRepository;


    @PostMapping
    public ResponseEntity<Object> create() throws Exception
    {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Session session = new Session();
        session.setUsername(principal.getUsername());
        session.setSessionId(UUID.randomUUID().toString().replace("-",""));
        long currentTime = System.currentTimeMillis();
        session.setCreationTime(currentTime);
        session.setLastAccessedTime(currentTime);
        session.setExpiryTime(currentTime + (30 * 60 * 1000));
        ObjectMapper mapper = new ObjectMapper();
        session.setUserDetails(mapper.writeValueAsString(principal));
        sessionRepository.save(session);
        //Create resource location
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(session.getSessionId())
                .toUri();

        //Send location in response
        return ResponseEntity.created(location).build();
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Object> renew(@PathVariable("id") String sid, @RequestHeader("Authorization") String auth) {
        Session session = sessionRepository.findBySessionId(sid);
        if(null == session) {
            return ResponseEntity.notFound().build();
        }
        long currentTime = System.currentTimeMillis();
        session.setLastAccessedTime(currentTime);
        session.setExpiryTime(currentTime + (30 * 60 * 1000));
        sessionRepository.save(session);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Object> getSession(@PathVariable("id") String sid, @RequestHeader("Authorization") String auth) {
        Session session = sessionRepository.findBySessionId(sid);
        if(null == session) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(session);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Object> invalidate(@PathVariable("id") String sid, @RequestHeader("Authorization") String auth) {
        Session session = sessionRepository.findBySessionId(sid);
        if(null == session) {
            return ResponseEntity.notFound().build();
        }
        sessionRepository.deleteById(session.getId());
        return ResponseEntity.noContent().build();
    }


}
