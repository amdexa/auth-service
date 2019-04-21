package com.amdexa.auth.service.session;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(path = "/auth/session")
public class SessionController {


    @PostMapping
    public ResponseEntity<Object> create()
    {

        //Create resource location
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand("1")
                .toUri();

        //Send location in response
        return ResponseEntity.created(location).build();
    }

    @PutMapping(path = "/new")
    public ResponseEntity<Object> renew()
    { return ResponseEntity.noContent().build();
    }


}
