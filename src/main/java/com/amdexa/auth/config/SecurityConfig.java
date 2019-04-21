package com.amdexa.auth.config;

import com.amdexa.auth.service.GroupService;
import com.amdexa.auth.service.UserService;
import com.amdexa.auth.service.repository.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private HttpServletRequest request;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/auth/session")
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .x509()
                .userDetailsService(userDetailsService())
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                .and()
                .httpBasic()
                .and()
                .csrf()
                .disable();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            com.amdexa.auth.service.repository.User user = userService.getUser(username);
            groupService.getGroup("user");
            if (null != user) {
                if (null != request.getAttribute("javax.servlet.request.X509Certificate")) {
                    return new User(username, "none",
                            AuthorityUtils
                                    .commaSeparatedStringToAuthorityList("USER"));
                } else {
                    User.UserBuilder users = User.withDefaultPasswordEncoder();
                    return users.username(username).password("password").roles("USER", "ADMIN").build();
                }
            } else {
                throw new UsernameNotFoundException(String.format("User %s not found", username));

            }

        };

    }

}
