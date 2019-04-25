package com.amdexa.auth.service.session;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class SessionAuthenticationFilter extends GenericFilterBean {


    private AuthenticationManager authenticationManager;

    public SessionAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) req;
        HttpServletResponse httpResponse = (HttpServletResponse) res;
        Optional<String> token = Optional.ofNullable(httpRequest.getHeader("Authorization"));

        if (token.isPresent() && token.get().startsWith("Bearer ")) {
            String authToken = token.get().replaceAll("^Bearer\\s", "");
            PreAuthenticatedAuthenticationToken requestAuthentication = new PreAuthenticatedAuthenticationToken(Optional.ofNullable(authToken), null);
            Authentication responseAuthentication = authenticationManager.authenticate(requestAuthentication);
            SecurityContextHolder.getContext().setAuthentication(responseAuthentication);
        } else {
            SecurityContextHolder.clearContext();
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No valid authorization header present");
        }

        chain.doFilter(req, res);
    }
}
