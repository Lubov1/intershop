package ru.yandex.practicum.intershop.services;

import javassist.NotFoundException;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.practicum.intershop.SecurityUtils;
import ru.yandex.practicum.intershop.client.ClientService;
import ru.yandex.practicum.intershop.dao.BasketItem;
import ru.yandex.practicum.intershop.dao.Product;
import ru.yandex.practicum.intershop.dto.Item;
import ru.yandex.practicum.intershop.repositories.CartRepository;
import ru.yandex.practicum.intershop.repositories.OrderRepository;
import ru.yandex.practicum.intershop.repositories.ProductorderRepository;


import java.math.BigDecimal;
import java.util.List;

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
        String userName = "user1";
        when(cartRepository.findByProductIdAndUserName(anyLong(), anyString()))
                .thenReturn(Mono.defer(() -> Mono.just(new BasketItem(id, 6, userName))));
//        when(SecurityUtils.currentUsername()).thenReturn(Mono.just(userName));
        when(cartRepository.save(any(BasketItem.class)))
                .thenAnswer(invocation -> {
                    BasketItem item = invocation.getArgument(0);
                    return Mono.just(item);
                });
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userName, userName, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        StepVerifier.create(
                cartService.changeQuantity(id, "plus")
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth))
        )
                .expectNext(7)
                .verifyComplete();

        verify(cartRepository, times(1)).findByProductIdAndUserName(eq(id), any());
        verify(cartRepository, times(1)).save(any(BasketItem.class));
        verify(cartRepository, never()).insert(anyLong(), anyInt(), anyString());
    }

    @Test
    void changeQuantityMinus() {
        Long id = 1L;
        String userName = "user1";
        when(cartRepository.findByProductIdAndUserName(anyLong(), anyString()))
                .thenReturn(Mono.defer(() -> Mono.just(new BasketItem(id, 6, userName))));

        when(cartRepository.save(any(BasketItem.class)))
                .thenAnswer(invocation -> {
                    BasketItem item = invocation.getArgument(0);
                    return Mono.just(item);
                });
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userName, userName, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        StepVerifier.create(
                cartService.changeQuantity(id, "minus")
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth))
        )
                .expectNext(5)
                .verifyComplete();

        verify(cartRepository, times(1)).findByProductIdAndUserName(id, userName);
        verify(cartRepository, times(1)).save(any(BasketItem.class));
        verify(cartRepository, never()).insert(anyLong(), anyInt(), anyString());
    }

    @Test
    void changeQuantityNewItem() {
        Long id = 1L;
        String userName = "user1";
        Mono<BasketItem> basketItem = Mono.empty();
        when(cartRepository.findByProductIdAndUserName(Mockito.anyLong(), anyString())).thenReturn(basketItem);
        when(cartRepository.insert(Mockito.anyLong(), Mockito.anyInt(), anyString())).thenReturn(Mono.just(new BasketItem(id, 1, userName)));

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userName, userName, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        StepVerifier.create(cartService.changeQuantity(id, "plus")
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
                .expectNext(1)
                .verifyComplete();

        verify(cartRepository, times(1)).findByProductIdAndUserName(eq(id), eq(userName));
        verify(cartRepository, never()).save(any(BasketItem.class));
        verify(cartRepository, times(1)).insert(anyLong(), anyInt(), anyString());
    }

    @Test
    void getTotalPrice() {
        Product product = new Product("Первый продукт", "Описание", BigDecimal.ONE, null, new BasketItem(1L, 6, ""));

        Item basketItem1 = new Item(product, new BasketItem(1L, 6, "name"));
        Item basketItem2 = new Item(product, new BasketItem(2L,1, "name"));
        List<Item> items = List.of(basketItem1, basketItem2);
        assertEquals(BigDecimal.valueOf(7), cartService.getTotalPrice(items));

    }

}