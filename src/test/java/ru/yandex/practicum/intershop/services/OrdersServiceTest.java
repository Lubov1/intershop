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
import ru.yandex.practicum.intershop.dao.Orders;
import ru.yandex.practicum.intershop.dao.Product;
import ru.yandex.practicum.intershop.dao.Productorder;
import ru.yandex.practicum.intershop.dto.Order;
import ru.yandex.practicum.intershop.dto.ProductDto;
import ru.yandex.practicum.intershop.repositories.OrderRepository;
import ru.yandex.practicum.intershop.repositories.ProductorderRepository;
import org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = OrdersService.class)
class OrdersServiceTest {

    @Autowired
    private OrdersService ordersService;

    @MockitoBean
    private OrderRepository orderRepository;

    @MockitoBean
    private ProductorderRepository productorderRepository;
    @Test
    void saveOrder() {
        Product product = new Product(1L, "Первый продукт", "Описание", BigDecimal.ONE, null, new BasketItem(1L, 6,null));

        BasketItem basketItem1 = new BasketItem(1L, 1, product);
        BasketItem basketItem2 = new BasketItem(2L, 1, product);
        List<BasketItem> items = List.of(basketItem1, basketItem2);
        when(orderRepository.save(any())).thenReturn(new Orders(1L, null,List.of()));

        ordersService.saveOrder(items);

        verify(orderRepository, times(1)).save(any());
    }

    @Test
    void getOrder() throws NotFoundException {
        Long id = 1L;
        Orders orders = new Orders(id, BigDecimal.ONE,List.of());
        orders.setId(id);
        Order order = new Order(id, BigDecimal.ONE,List.of());

        when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(orders));
        
        assertEquals(order.getId(), ordersService.getOrder(id).getId());
        assertEquals(order.getItems().size(), ordersService.getOrder(id).getItems().size());
    }

    @Test
    void getOrders() {
        Long id = 1L;
        Orders orders = new Orders(id, BigDecimal.ONE,List.of());
        orders.setId(id);
        Order order = new Order(id, BigDecimal.ONE,List.of());

        when(orderRepository.findAll()).thenReturn(List.of(orders, orders));

        List<Order> orders1 = ordersService.getOrders();
        assertEquals(2, orders1.size());
        assertEquals(BigDecimal.ONE, orders1.get(0).getPrice());

    }

    @Test
    void getPrice() {
        Product product = new Product(1L, "Первый продукт", "Описание", BigDecimal.ONE, null, new BasketItem(1L, 6,null));

        BasketItem basketItem1 = new BasketItem(1L, 3, product);
        BasketItem basketItem2 = new BasketItem(2L, 1, product);
        List<BasketItem> items = List.of(basketItem1, basketItem2);
        assertEquals(BigDecimal.valueOf(4), ordersService.getPrice(items));
        
    }
}