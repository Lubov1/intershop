package ru.yandex.practicum.intershop.services;

import javassist.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.intershop.dao.BasketItem;
import ru.yandex.practicum.intershop.dao.Orders;
import ru.yandex.practicum.intershop.dao.Productorder;
import ru.yandex.practicum.intershop.dto.Order;
import ru.yandex.practicum.intershop.dto.OrderItem;
import ru.yandex.practicum.intershop.repositories.OrderRepository;
import ru.yandex.practicum.intershop.repositories.ProductRepository;
import ru.yandex.practicum.intershop.repositories.ProductorderRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OrdersService {
    private OrderRepository orderRepository;
    private ProductorderRepository productorderRepository;

    @Transactional
    public Long saveOrder(List<BasketItem> items) {

        Orders order = new Orders();
        List<Productorder> productorders = items.stream()
                .map(a-> new Productorder(
                a.getQuantity(), order, a.getProduct())).toList();

        order.setProductorders(productorders);
        order.setPrice(getPrice(items));

        return orderRepository.save(order).getId();
    }

    @Transactional(readOnly = true)
    public Order getOrder(Long id) throws NotFoundException {
        Optional<Orders> order = orderRepository.findById(id);
        if (order.isPresent()) {
            return new Order(order.get(), getOrderItems(order.get().getId()));
        } else {
            throw new NotFoundException("Order with id " + id + "was not found");
        }
    }

    @Transactional(readOnly = true)
    public List<Order> getOrders() {
        return orderRepository.findAll().stream()
                .map(orders->new Order(orders, getOrderItems(orders.getId()))).toList();
    }

    @Transactional(readOnly = true)
    List<OrderItem> getOrderItems(Long orderId){
        return productorderRepository.findAllByOrderId(orderId).stream()
                .map(OrderItem::new).toList();
    }

    public BigDecimal getPrice(List<BasketItem> items){
        return items.stream().map(BasketItem::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
