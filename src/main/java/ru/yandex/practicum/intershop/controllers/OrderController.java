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
import ru.yandex.practicum.intershop.services.OrdersService;

@Controller
@RequestMapping("/orders")
@AllArgsConstructor
public class OrderController {
    private OrdersService ordersService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public String getOrders(Model model) {
        model.addAttribute("orders", ordersService.getOrders());
        return "orders";
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/order/{id}")
    public String getOrder(Model model, @PathVariable Long id) throws NotFoundException {
        model.addAttribute("order", ordersService.getOrder(id));
        return "order";
    }
}
