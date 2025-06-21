package ru.yandex.practicum.intershop.services;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class SessionRegistry {
    private ReactiveRedisTemplate<String, String> userSessionMap;


    public Mono<Boolean> isSessionAllowed(String username, String newSessionId) {
        return userSessionMap
                .opsForValue()
                .get("Username:"+username)
                .map(existingSessionId->existingSessionId == null || existingSessionId.equals(newSessionId));
    }

    public Mono<Void> registerSession(String username, String sessionId) {
        return userSessionMap
                .opsForValue()
                .set("Username:"+username, sessionId)
                .then(Mono.empty());
    }

    public Mono<Void> unregisterSession(String username, String sessionId) {
        return userSessionMap
                .opsForValue()
                .delete("Username:"+username)
                .then();
    }
}