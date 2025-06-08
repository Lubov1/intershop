package ru.yandex.practicum.intershop.controllers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.client.ClientService;
import ru.yandex.practicum.intershop.dto.Action;
import ru.yandex.practicum.intershop.services.BalanceStatus;
import ru.yandex.practicum.intershop.services.CartService;

import java.math.BigDecimal;


@Controller
@RequestMapping("/cart")
@AllArgsConstructor
public class CartController {
    private CartService cartService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Mono<String> getCart(Model model) {
        return cartService.getBasketItems().collectList()
                .flatMap(basketItems-> {
                    model.addAttribute("items", basketItems);
                    model.addAttribute("price", cartService.getTotalPrice(basketItems));
                    return cartService.orderIsAllowed((BigDecimal) model.getAttribute("price"))
                            .map(balanceStatus -> {
                                model.addAttribute("paymentServiceNotAvailable", balanceStatus.equals(BalanceStatus.SERVICE_UNAVAILABLE));
                                model.addAttribute("orderingAllowed", balanceStatus.equals(BalanceStatus.OK));
                                return "cart";
                            });
                });
    }

    @PostMapping(value = "/update")
    public Mono<String> addToCart(@ModelAttribute Action action,
                                  @RequestHeader(value = "Referer", required = false) String referer) {
        return cartService.changeQuantity(action.productId(), action.action())
                .map(quantity->"redirect:" + (referer != null ? referer : "/cart"));
    }

    @PostMapping("/buy")
    public Mono<String> buy(Model model) {
        return cartService.makeOrder()
        .doOnNext(id->model.addAttribute("redirect", true))
                .map(orderId ->"redirect:/orders/order/"+orderId+"?redirect=true");
    }
}
