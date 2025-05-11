package ru.yandex.practicum.intershop.controllers;

import javassist.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.intershop.dao.Orders;
import ru.yandex.practicum.intershop.services.OrdersService;

@Controller("/orders")
@AllArgsConstructor
public class OrderController {
    private OrdersService ordersService;

    @GetMapping
    public String getOrders(Model model) {
        model.addAttribute("orders", ordersService.getOrders());
        return "orders";
    }

    @GetMapping("/{id}")
    public String getOrders(Model model, @PathVariable Long id) throws NotFoundException {
        model.addAttribute("order", ordersService.getOrder(id));
        return "orders";
    }
}
