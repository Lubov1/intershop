package ru.yandex.practicum.intershop.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.HttpStatusReturningServerLogoutSuccessHandler;
import reactor.core.publisher.Mono;
import org.springframework.web.server.WebSession;
import ru.yandex.practicum.intershop.dao.UserDao;
import org.springframework.security.authorization.AuthorizationDecision;
import ru.yandex.practicum.intershop.repositories.UserRepository;
import ru.yandex.practicum.intershop.services.SessionRegistry;
import ru.yandex.practicum.intershop.services.UserDetailsServiceImpl;

import java.net.URI;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableReactiveMethodSecurity
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, SessionRegistry sessionRegistry) {
        return http
                .formLogin(form -> form
                        .authenticationSuccessHandler(new CustomAuthenticationSuccessHandler(sessionRegistry)))
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/main", "/login", "/register").permitAll()
                        .anyExchange().access((mono, context) ->
                                mono.map(auth -> auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken))
                                        .map(AuthorizationDecision::new)))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .logout(logoutSpec -> logoutSpec
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((exchange, authentication) ->
                                exchange.getExchange()
                                        .getSession()
                                        .zipWith(ReactiveSecurityContextHolder.getContext()
                                                .map(securityContext -> securityContext.getAuthentication().getName()))
                                        .flatMap(tuple -> sessionRegistry
                                                    .unregisterSession(tuple.getT1().getId(), tuple.getT2()))
                                        .then(Mono.defer(() -> {
                                            ServerHttpResponse response = exchange.getExchange().getResponse();
                                            response.setStatusCode(HttpStatus.SEE_OTHER);
                                            response.getHeaders().setLocation(URI.create("/main"));
                                            return response.setComplete();
                                        }))))
                .build();
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        UserDetailsServiceImpl service = new UserDetailsServiceImpl(userRepository, passwordEncoder);
        return service;
    }

    @Bean PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

class CustomAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    private final SessionRegistry sessionRegistry;

    public CustomAuthenticationSuccessHandler(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange exchange, Authentication authentication) {
        return exchange
                .getExchange()
                .getSession()
                .flatMap(session -> {
                    String username = authentication.getName();
                    String sessionId = session.getId();
                    return sessionRegistry
                            .isSessionAllowed(username, sessionId)
                            .flatMap(allowed-> {
                                if (!allowed) {
                                    return session.invalidate()
                                            .then(exchange.getExchange().getResponse().setComplete());
                                }

                                sessionRegistry.registerSession(username, sessionId);
                                return Mono.empty();
                            })
                            .flatMap(m->{
                                ServerHttpResponse response = exchange.getExchange().getResponse();
                                response.setStatusCode(HttpStatus.SEE_OTHER);
                                response.getHeaders().setLocation(URI.create("/main"));
                                return response.setComplete();});
                });
    }
}
