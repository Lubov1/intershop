package ru.yandex.practicum.intershop.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.dao.UserDao;
import ru.yandex.practicum.intershop.dto.UserDto;
import ru.yandex.practicum.intershop.services.UserDetailsServiceImpl;

@Controller
public class UserController {
    @Autowired
    ReactiveUserDetailsService userDetailsService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/register")
    public Mono<String> register() {
        return Mono.just("registration");
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/register")
    public Mono<String> register(@ModelAttribute UserDto user) {
        return ((UserDetailsServiceImpl) userDetailsService)
                .saveUser(new UserDao(user))
                .map(userDao -> "redirect:/main");
    }
}
