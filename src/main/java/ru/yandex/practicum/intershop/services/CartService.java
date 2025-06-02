package ru.yandex.practicum.intershop.services;


import javassist.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.dao.BasketItem;
import ru.yandex.practicum.intershop.dao.Orders;
import ru.yandex.practicum.intershop.dao.Productorder;
import ru.yandex.practicum.intershop.dto.Item;
import ru.yandex.practicum.intershop.repositories.CartRepository;
import ru.yandex.practicum.intershop.repositories.OrderRepository;
import ru.yandex.practicum.intershop.repositories.ProductRepository;
import ru.yandex.practicum.intershop.repositories.ProductorderRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class CartService {

    private final OrderRepository orderRepository;
    private CartRepository cartRepository;
    private ProductRepository productRepository;
    private ProductorderRepository productorderRepository;

    @Transactional
    public Flux<Item> getBasketItems(){
        return cartRepository
                .findAll()
                .flatMap(basketItem -> productRepository.findById(basketItem.getProductId())
                .map(product -> new Item(product, basketItem)));
    }

    @Transactional
    public Mono<Integer> changeQuantity(long id, String action) {
        return switch (action) {
            case "plus" -> addItem(id);
            case "minus" -> reduceItem(id);
            default -> Mono.error(new NotFoundException("action not found"));
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
                .flatMap(basketItem -> productRepository.findById(basketItem.getProductId())
                        .map(product -> new Item(product, basketItem)))
                .collectList()
                .flatMap(basketItems ->  orderRepository.save(new Orders(getTotalPrice(basketItems)))
                        .flatMap(order ->
                        productorderRepository.saveAll(basketItems.stream()
                                .map(basketItem -> new Productorder(basketItem.getId(), order.getId(), basketItem.getQuantity()))
                                .toList()).then(cartRepository.deleteAll()).thenReturn(order.getId())
                        ));
    }
}
