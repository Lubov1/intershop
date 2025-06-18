package ru.yandex.practicum.intershop.services;

import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.dao.UserDao;
import ru.yandex.practicum.intershop.dto.UserDetailsImpl;
import ru.yandex.practicum.intershop.repositories.UserRepository;

import java.util.List;


@AllArgsConstructor
public class UserDetailsServiceImpl implements ReactiveUserDetailsService {

    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(UserDetailsImpl::new)
                .map(userDetails -> (UserDetails) userDetails)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException(username)));

    }

    public Mono<UserDao> saveUser(UserDao user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        System.out.println("hash "+ user.getPassword());
        user.setAuthority("ROLE_USER");
        return userRepository.save(user);
    }
}
