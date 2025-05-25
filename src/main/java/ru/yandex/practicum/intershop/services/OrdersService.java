package ru.yandex.practicum.intershop.services;

import javassist.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.dto.Order;
import ru.yandex.practicum.intershop.dto.OrderItem;
import ru.yandex.practicum.intershop.repositories.OrderRepository;
import ru.yandex.practicum.intershop.repositories.ProductRepository;
import ru.yandex.practicum.intershop.repositories.ProductorderRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class OrdersService {
    private OrderRepository orderRepository;
    private ProductorderRepository productorderRepository;
    private ProductRepository productRepository;

    @Transactional
    public Mono<Order> getOrder(Long id) {
        return productorderRepository.findByOrderId(id)
                .flatMap(productorders ->
                        productRepository.findById(productorders.getProductId())
                                .map(product -> new OrderItem(id, product, productorders.getQuantity())))
                .collectList()
                .zipWith(orderRepository
                        .findById(id)
                        .switchIfEmpty(Mono.error(new NotFoundException("Order not found"))))
                .map(tuple->new Order(id, tuple.getT2().getPrice(), tuple.getT1()));
    }

    @Transactional
    public Mono<List<Order>> getOrders() {
        return orderRepository.findAll().flatMap(orders -> getOrder(orders.getId())).collectList();
    }
}
