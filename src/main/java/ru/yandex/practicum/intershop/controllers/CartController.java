package ru.yandex.practicum.intershop.controllers;

import javassist.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.intershop.dao.BasketItem;
import ru.yandex.practicum.intershop.services.CartService;
import ru.yandex.practicum.intershop.services.OrdersService;

import java.util.List;

@Controller
@RequestMapping("/cart")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CartController {
    private final OrdersService ordersService;
    private CartService cartService;
    @GetMapping
    public String getCart(Model model) {
        List<BasketItem> basketItems = cartService.getBasketItems();
        model.addAttribute("basketItems", basketItems);
        model.addAttribute("price", cartService.getTotalPrice());
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
    public String buy(Model model){
        List<BasketItem> basketItems = cartService.getBasketItems();
        Long orderId = ordersService.saveOrder(basketItems);
        model.addAttribute("redirect", true);
        return "redirect:/orders/order/"+orderId;
    }
}
