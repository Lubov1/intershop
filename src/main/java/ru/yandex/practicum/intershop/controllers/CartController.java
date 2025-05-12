package ru.yandex.practicum.intershop.controllers;

import javassist.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.intershop.dto.Item;
import ru.yandex.practicum.intershop.services.CartService;

import java.util.List;

@Controller
@RequestMapping("/cart")
@AllArgsConstructor
public class CartController {
    private CartService cartService;
    @GetMapping
    public String getCart(Model model) {
        List<Item> basketItems = cartService.getBasketItems();
        model.addAttribute("items", basketItems);
        model.addAttribute("price", cartService.getTotalPrice(basketItems));
        return "cart";
    }

    @PostMapping("/update")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam String action,
                            @RequestHeader(value = "Referer", required = false) String referer,
                            Model model) throws NotFoundException {

        int quantity = cartService.changeQuantity(productId, action);
        model.addAttribute("quantity", quantity);
        return "redirect:" + (referer != null ? referer : "/cart");
    }

    @PostMapping("/buy")
    public String buy(Model model) throws NotFoundException{
        Long orderId = cartService.makeOrder();

        model.addAttribute("redirect", true);
        return "redirect:/orders/order/"+orderId;
    }
}
