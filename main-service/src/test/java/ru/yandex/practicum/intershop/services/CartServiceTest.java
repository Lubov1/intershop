package ru.yandex.practicum.intershop.services;

import javassist.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.practicum.intershop.client.ClientService;
import ru.yandex.practicum.intershop.dao.BasketItem;
import ru.yandex.practicum.intershop.dao.Product;
import ru.yandex.practicum.intershop.dto.Item;
import ru.yandex.practicum.intershop.repositories.CartRepository;
import ru.yandex.practicum.intershop.repositories.OrderRepository;
import ru.yandex.practicum.intershop.repositories.ProductRepository;
import ru.yandex.practicum.intershop.repositories.ProductorderRepository;
import ru.yandex.practicum.intershop.services.CartService;
import ru.yandex.practicum.intershop.services.OrdersService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CartService.class})
class CartServiceTest {
    @Autowired
    private CartService cartService;

    @MockitoBean
    private ClientService clientService;

    @MockitoBean
    private CartRepository cartRepository;

    @MockitoBean
    private OrderRepository orderRepository;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    ProductorderRepository productorderRepository;

    @Test
    void getBasketItems() {
    }
    @Test
    void changeQuantityPlus_existingItem_withStepVerifier() {
        Long id = 1L;

        when(cartRepository.findByProductId(anyLong()))
                .thenReturn(Mono.defer(() -> Mono.just(new BasketItem(id, 6))));

        when(cartRepository.save(any(BasketItem.class)))
                .thenAnswer(invocation -> {
                    BasketItem item = invocation.getArgument(0);
                    return Mono.just(item);
                });
        StepVerifier.create(cartService.changeQuantity(id, "plus"))
                .expectNext(7)
                .verifyComplete();

        verify(cartRepository, times(1)).findByProductId(id);
        verify(cartRepository, times(1)).save(any(BasketItem.class));
        verify(cartRepository, never()).insert(anyLong(), anyInt());
    }

    @Test
    void changeQuantityMinus() {
        Long id = 1L;

        when(cartRepository.findByProductId(anyLong()))
                .thenReturn(Mono.defer(() -> Mono.just(new BasketItem(id, 6))));

        when(cartRepository.save(any(BasketItem.class)))
                .thenAnswer(invocation -> {
                    BasketItem item = invocation.getArgument(0);
                    return Mono.just(item);
                });
        StepVerifier.create(cartService.changeQuantity(id, "minus"))
                .expectNext(5)
                .verifyComplete();

        verify(cartRepository, times(1)).findByProductId(id);
        verify(cartRepository, times(1)).save(any(BasketItem.class));
        verify(cartRepository, never()).insert(anyLong(), anyInt());
    }

    @Test
    void changeQuantityNewItem() {
        Long id = 1L;
        Mono<BasketItem> basketItem = Mono.empty();
        when(cartRepository.findByProductId(Mockito.anyLong())).thenReturn(basketItem);
        when(cartRepository.insert(Mockito.anyLong(), Mockito.anyInt())).thenReturn(Mono.just(new BasketItem(id, 1)));

        StepVerifier.create(cartService.changeQuantity(id, "plus"))
                .expectNext(1)
                .verifyComplete();

        verify(cartRepository, times(1)).findByProductId(id);
        verify(cartRepository, never()).save(any(BasketItem.class));
        verify(cartRepository, times(1)).insert(anyLong(), anyInt());
    }

    @Test
    void getTotalPrice() {
        Product product = new Product("Первый продукт", "Описание", BigDecimal.ONE, null, new BasketItem(1L, 6));

        Item basketItem1 = new Item(product, new BasketItem(1L, 6));
        Item basketItem2 = new Item(product, new BasketItem(2L,1));
        List<Item> items = List.of(basketItem1, basketItem2);
        assertEquals(BigDecimal.valueOf(7), cartService.getTotalPrice(items));

    }

    @Test
    void makeOrder() {
    }
}