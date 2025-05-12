package ru.yandex.practicum.intershop.controllers;

import javassist.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.yandex.practicum.intershop.services.OrdersService;

@Controller
@RequestMapping("/orders")
@AllArgsConstructor
public class OrderController {
    private OrdersService ordersService;

    @GetMapping
    public String getOrders(Model model) {
        model.addAttribute("orders", ordersService.getOrders());
        return "orders";
    }

    @GetMapping("/order/{id}")
    public String getOrders(Model model, @PathVariable Long id) throws NotFoundException {
        model.addAttribute("order", ordersService.getOrder(id));
        return "order";
    }
}
