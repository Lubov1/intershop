package ru.yandex.practicum.intershop.services;

import javassist.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.intershop.dao.BasketItem;
import ru.yandex.practicum.intershop.dao.Product;
import ru.yandex.practicum.intershop.dto.Item;
import ru.yandex.practicum.intershop.repositories.CartRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CartService.class})
class CartServiceTest {
    @Autowired
    private CartService cartService;

    @MockitoBean
    private CartRepository cartRepository;

    @MockitoBean
    private OrdersService ordersService;

    @Test
    void getBasketItems() {
    }

    @Test
    void changeQuantity() throws NotFoundException {
        Long id = 1L;
        Optional<BasketItem> basketItem = Optional.of(new BasketItem(id, 6, null));
        when(cartRepository.findById(Mockito.anyLong())).thenReturn(basketItem);

        int quantity = cartService.changeQuantity(id, "plus");

        assertEquals(7, quantity);
    }

    @Test
    void getTotalPrice() {
        Product product = new Product(1L, "Первый продукт", "Описание", BigDecimal.ONE, null, new BasketItem(1L, 6,null));

        Item basketItem1 = new Item(product, 3);
        Item basketItem2 = new Item(product, 1);
        List<Item> items = List.of(basketItem1, basketItem2);
        assertEquals(BigDecimal.valueOf(4), cartService.getTotalPrice(items));

    }

    @Test
    void makeOrder() {
    }
}