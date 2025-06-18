package ru.yandex.practicum.intershop.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.HttpStatusReturningServerLogoutSuccessHandler;
import ru.yandex.practicum.intershop.dao.UserDao;
import ru.yandex.practicum.intershop.repositories.UserRepository;
import ru.yandex.practicum.intershop.services.UserDetailsServiceImpl;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .formLogin(withDefaults())
                .authorizeExchange(exchanges -> exchanges.anyExchange().authenticated())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .logout(logoutSpec -> logoutSpec
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(new HttpStatusReturningServerLogoutSuccessHandler()) )
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
