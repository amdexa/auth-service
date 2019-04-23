package com.amdexa.auth.service.session;

import com.amdexa.auth.service.session.model.Session;
import com.amdexa.auth.service.session.repository.SessionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

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
        ObjectMapper mapper = new ObjectMapper();
        session.setUserDetails(mapper.writeValueAsString(principal));
        sessionRepository.save(session);
        //Create resource location
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(session.getId())
                .toUri();

        //Send location in response
        return ResponseEntity.created(location).build();
    }

    @PutMapping(path = "/new")
    public ResponseEntity<Object> renew()
    { return ResponseEntity.noContent().build();
    }


}
