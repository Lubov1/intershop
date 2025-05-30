package ru.yandex.practicum.intershop.services;


import javassist.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.intershop.dao.BasketItem;
import ru.yandex.practicum.intershop.dao.Product;
import ru.yandex.practicum.intershop.dto.Item;
import ru.yandex.practicum.intershop.repositories.CartRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CartService {

    private CartRepository cartRepository;
    private OrdersService ordersService;

    @Transactional(readOnly = true)
    public List<Item> getBasketItems(){
        return cartRepository.findAll().stream().map(basketItem -> new Item(basketItem.getProduct(),
                basketItem.getQuantity())).toList();
    }

    @Transactional
    public int changeQuantity(long id, String action) throws NotFoundException {
        Optional<BasketItem> basketItem = cartRepository.findById(id);
        int quantity;
        BasketItem item;
        return switch (action) {
            case "plus" -> {
                if (basketItem.isPresent()) {
                    item = basketItem.get();
                    item.setQuantity(item.getQuantity() + 1);
                } else {
                    item = new BasketItem(id, 1);
                }
                quantity = item.getQuantity();
                cartRepository.save(item);
                yield quantity;
            }
            case "minus" -> {
                if (basketItem.isPresent()) {
                    item = basketItem.get();
                    if (item.getQuantity() > 1) {
                        item.setQuantity(item.getQuantity() - 1);
                        quantity = item.getQuantity();
                        cartRepository.save(item);
                        yield quantity;
                    } else {
                        item.getProduct().setBasketItem(null);
                        cartRepository.delete(item);
                        yield 0;
                    }
                } else {
                    yield 0;
                }
            }
            default -> throw new NotFoundException("Action not recognized");
        };
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalPrice(List<Item> items) {
        return items.stream()
                .map(Item::getPrice).reduce(BigDecimal.valueOf(0), BigDecimal::add);
    }

    @Transactional
    public Long makeOrder() {
        List<BasketItem> basketItems = cartRepository.findAll();
        Long orderId = ordersService.saveOrder(basketItems);
        cartRepository.deleteAll(basketItems);
        return orderId;
    }
}
