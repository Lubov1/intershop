package ru.yandex.practicum.intershop.repositories;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.dao.UserDao;

public interface UserRepository extends R2dbcRepository<UserDao, Long> {
    public Mono<UserDao> findByUsername(String username);
}
