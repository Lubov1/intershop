package ru.yandex.practicum.intershop.services;

import javassist.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.dao.BasketItem;
import ru.yandex.practicum.intershop.dao.Orders;
import ru.yandex.practicum.intershop.dao.Product;
import ru.yandex.practicum.intershop.dto.Order;
import ru.yandex.practicum.intershop.repositories.OrderRepository;

import java.math.BigDecimal;
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

//    @Test
//    void getOrder() throws NotFoundException {
//        Long id = 1L;
//        Orders orders = new Orders(id, BigDecimal.ONE);
//        orders.setId(id);
//        Order order = new Order(id, BigDecimal.ONE,List.of());
//
//        when(orderRepository.findById(Mockito.anyLong())).thenReturn(Mono.just(orders));
//        Order order1 = ordersService.getOrder(id).block();
//        assertEquals(order.getId(), order1.getId());
//        assertEquals(order.getItems().size(), order1.getItems().size());
//    }
//
//    @Test
//    void getOrders() {
//        Long id = 1L;
//        Orders orders = new Orders(id, BigDecimal.ONE);
//        orders.setId(id);
//        Orders order = new Orders(id, BigDecimal.ONE);
//
//        when(orderRepository.findAll()).thenReturn(Flux.just(orders, orders));
//
//        List<Order> orders1 = ordersService.getOrders().block();
//        assertEquals(2, orders1.size());
//        assertEquals(BigDecimal.ONE, orders1.get(0).getPrice());
//
//    }
}