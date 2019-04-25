package com.amdexa.auth.service.session;

import com.amdexa.auth.service.session.model.Session;
import com.amdexa.auth.service.session.repository.SessionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Optional;

public class TokenAuthenticationProvider implements AuthenticationProvider {

    private SessionRepository sessionRepository;

    public TokenAuthenticationProvider(SessionRepository sessionRepository) {
       this.sessionRepository = sessionRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        try {
            Optional<String> token = (Optional) authentication.getPrincipal();
            if (!token.isPresent() || token.get().isEmpty()) {
                throw new BadCredentialsException("Invalid token");
            }
            Session session = sessionRepository.findBySessionId(token.get());
            if (null == session) {
                throw new BadCredentialsException("Invalid token");
            }
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(session.getUserDetails());
            StringBuilder roles = new StringBuilder();
            ((ArrayNode) jsonNode.get("authorities")).forEach(auth -> {
                roles.append(auth.get("authority").asText());
                roles.append(",");
            });
            return new UsernamePasswordAuthenticationToken
                    (session.getUsername(), "none",
                            AuthorityUtils.commaSeparatedStringToAuthorityList(StringUtils.trimTrailingCharacter(roles.toString(),',')));
        } catch (IOException e) {
           throw  new SessionAuthenticationException("Invalid token");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(PreAuthenticatedAuthenticationToken.class);
    }
}

