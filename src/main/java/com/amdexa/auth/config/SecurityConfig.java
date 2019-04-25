package com.amdexa.auth.config;

import com.amdexa.auth.service.GroupService;
import com.amdexa.auth.service.UserService;
import com.amdexa.auth.service.session.SessionAuthenticationFilter;
import com.amdexa.auth.service.session.TokenAuthenticationProvider;
import com.amdexa.auth.service.session.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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


    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            com.amdexa.auth.service.repository.User user = userService.getUser(username);
            if (null != user) {
                String[] roles = groupService.getGroups(username).stream().map(g -> g.getName().toUpperCase()).toArray(String[]::new);
                if (null != request.getAttribute("javax.servlet.request.X509Certificate")) {
                    return new User(username, "none",
                            AuthorityUtils
                                    .createAuthorityList(roles));
                } else {
                    String encodedPassword = Stream.of(user.getPassword().split(","))
                            .mapToInt(Integer::valueOf).mapToObj(c -> String.valueOf((char) c))
                            .collect(Collectors.joining());
                    return User.withUsername(username).password(encodedPassword.replace("{SSHA}", "{ldap}")).roles(roles).build();
                }
            } else {
                throw new UsernameNotFoundException(String.format("User %s not found", username));
            }

        };

    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService());
    }

    @Configuration
    @Order(1)
    public static class CreateSessionSecurityConfig extends WebSecurityConfigurerAdapter {


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

    }

    @Configuration
    @Order(2)
    public static class SessionTokenSecurityConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        SessionRepository sessionRepository;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.requestMatcher(new NegatedRequestMatcher(new AntPathRequestMatcher("/auth/session")))
                    .authorizeRequests()
                    .anyRequest()
                    .authenticated()
                    .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                    .and()
                    .csrf()
                    .disable();
            http.addFilterBefore(new SessionAuthenticationFilter(authenticationManager()), BasicAuthenticationFilter.class);


        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) {
            auth.authenticationProvider(tokenAuthenticationProvider());
        }

        @Bean
        public AuthenticationProvider tokenAuthenticationProvider() {
            return new TokenAuthenticationProvider(sessionRepository);
        }

    }

}
