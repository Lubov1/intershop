package ru.yandex.practicum.intershop.services;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.client.ClientService;
import ru.yandex.practicum.intershop.dao.BasketItem;
import ru.yandex.practicum.intershop.dao.Orders;
import ru.yandex.practicum.intershop.dao.Productorder;
import ru.yandex.practicum.intershop.dto.Item;
import ru.yandex.practicum.intershop.exceptions.ActionNotFoundException;
import ru.yandex.practicum.intershop.exceptions.PaymentServiceNotAvailableException;
import ru.yandex.practicum.intershop.exceptions.ProductNotFoundException;
import ru.yandex.practicum.intershop.repositories.CartRepository;
import ru.yandex.practicum.intershop.repositories.OrderRepository;
import ru.yandex.practicum.intershop.repositories.ProductorderRepository;

import java.math.BigDecimal;
import java.util.List;

import static ru.yandex.practicum.intershop.services.BalanceStatus.*;

@Service
@AllArgsConstructor
public class CartService {

    private final ClientService clientService;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductService productService;
    private final ProductorderRepository productorderRepository;


    @Transactional
    public Flux<Item> getBasketItems(){
        return cartRepository
                .findAll()
                .flatMap(basketItem -> productService.getProduct(basketItem.getProductId())
                        .map(product -> new Item(product, basketItem)));
    }

    @Transactional
    public Mono<Integer> changeQuantity(long id, String action) {
        return switch (action) {
            case "plus" -> addItem(id);
            case "minus" -> reduceItem(id);
            default -> Mono.error(new ActionNotFoundException());
        };
    }

    Mono<Integer> addItem(Long id) {
        return cartRepository.findByProductId(id)
                .flatMap(existing->{
                    existing.setQuantity(existing.getQuantity() + 1);
                    return cartRepository.save(existing);
                })
                .switchIfEmpty(Mono.defer(()->cartRepository.insert(id, 1)))
                .map(BasketItem::getQuantity);
    }

    Mono<Integer> reduceItem(Long id) {
        return cartRepository.findByProductId(id)
                .switchIfEmpty(Mono.error(new ProductNotFoundException("product not found")))
                .map(basketItem -> {
                    basketItem.setQuantity(basketItem.getQuantity() - 1);
                    return basketItem;
                })
                .flatMap(basketItem -> {
                    if (basketItem.getQuantity()==0) {
                        return cartRepository.delete(basketItem).thenReturn(basketItem);}
                    else {
                        return cartRepository.save(basketItem);
                    }
                })
                .map(BasketItem::getQuantity)
                .switchIfEmpty(Mono.just(0));
    }

    public BigDecimal getTotalPrice(List<Item> items) {
        return items.stream()
                .map(Item::getPrice).reduce(BigDecimal.valueOf(0), BigDecimal::add);
    }

    @Transactional
    public Mono<Long> makeOrder() {
        return cartRepository.findAll()
                .flatMap(basketItem -> productService.getProduct(basketItem.getProductId())
                        .map(product -> new Item(product, basketItem)))
                .collectList()
                .flatMap(items -> clientService.buyItems(getTotalPrice(items)).flatMap(balance-> {
                    if (balance.compareTo(BigDecimal.ZERO) < 0) {
                        return Mono.error(new RuntimeException("unsufficient balance"));
                    }
                    return saveOrder(items);
                }));
    }

    public Mono<BalanceStatus> orderIsAllowed(BigDecimal price) {
        return clientService.getBalance()
                .map(balance->balance.compareTo(price)>=0? OK : INSUFFICIENT_FUNDS)
                .onErrorResume(PaymentServiceNotAvailableException.class, ex -> Mono.just(SERVICE_UNAVAILABLE));
    }

    private Mono<Long> saveOrder(List<Item> items) {
        return orderRepository.save(new Orders(getTotalPrice(items)))
                .flatMap(order -> productorderRepository
                        .saveAll(items.stream()
                                .map(basketItem -> new Productorder(basketItem.getId(), order.getId(), basketItem.getQuantity()))
                                .toList())
                        .then(cartRepository.deleteAll())
                        .thenReturn(order.getId()));
    }
}
