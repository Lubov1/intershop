package ru.yandex.practicum.intershop.controllers;

import javassist.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.services.OrdersService;

@Controller
@RequestMapping("/orders")
@AllArgsConstructor
public class OrderController {
    private OrdersService ordersService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Mono<String> getOrders(Model model) {
        return ordersService.getOrders()
                .doOnNext(orders -> model.addAttribute("orders", orders))
                .map(order -> "orders");
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/order/{id}")
    public Mono<String> getOrder(Model model, @PathVariable Long id) throws NotFoundException {
        return ordersService.getOrder(id)
                        .doOnNext(order -> model.addAttribute("order", order))
                                .map(order -> "order");
    }
}
