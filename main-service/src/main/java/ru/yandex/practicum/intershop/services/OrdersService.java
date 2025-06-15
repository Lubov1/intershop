package ru.yandex.practicum.intershop.services;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.dao.Product;
import ru.yandex.practicum.intershop.dto.Order;
import ru.yandex.practicum.intershop.dto.OrderItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@Service
@AllArgsConstructor
public class OrdersService {
    private final DatabaseClient databaseClient;

    @Transactional
    public Mono<Order> getOrder(Long id) {
        String query = """
            SELECT 
                p.id AS product_id,
                p.name AS product_name,
                o.id AS order_id,
                p.description,
                p.image,
                p.price,
                po.quantity,
                o.user_name
            FROM 
                PRODUCTORDER po
            JOIN ORDERS o ON po.order_id = o.id
            JOIN PRODUCT p ON po.product_id = p.id
            WHERE po.order_id=:orderId
        """;
        return databaseClient.sql(query)
                .bind("orderId", id)
                .map(row-> Pair.of(row.get("user_name", String.class),
                        new OrderItem(
                        row.get("order_id", Long.class),
                        new Product(
                                row.get("product_id", Long.class),
                                row.get("product_name", String.class),
                                row.get("description", String.class),
                                row.get("price", BigDecimal.class),
                                row.get("image", byte[].class)),
                        row.get("quantity", Integer.class))
                                ))
                .all()
                .collectList()
                .flatMap(list -> {
                    if (list.isEmpty()) {
                        return Mono.error(new RuntimeException("Order not found"));
                    }
                    List<OrderItem> items = list.stream().map(Pair::getValue).toList();
                    String userName = list.getFirst().getKey();
                    return Mono.just(new Order(id, getTotalPrice(items), items, userName));
                });
    }

    @Transactional
    public Mono<List<Order>> getOrders() {
        String userName = "first";
        String query = """
            SELECT 
                p.id AS product_id,
                p.name AS product_name,
                o.id AS order_id,
                p.description,
                p.price,
                p.image,
                po.quantity,
                o.user_name
            FROM 
                PRODUCTORDER po
            JOIN PRODUCT p ON po.product_id = p.id
            JOIN ORDERS o ON po.order_id = o.id
            WHERE o.user_name=:userName
        """;

        return databaseClient.sql(query)
                .bind("userName", userName)
                .map(row -> new OrderItem(
                        row.get("order_id", Long.class),
                        new Product(
                            row.get("product_id", Long.class),
                            row.get("product_name", String.class),
                            row.get("description", String.class),
                            row.get("price", BigDecimal.class),
                            row.get("image", byte[].class)),
                        row.get("quantity", Integer.class)
                ))
                .all()
                .groupBy(OrderItem::getOrderId)
                .flatMap(grouped->grouped.collectList().map(list-> Map.entry(grouped.key(), list)))
                .map(e->new Order(e.getKey(), getTotalPrice(e.getValue()), e.getValue(), userName))
                .collectList();
    }

    public BigDecimal getTotalPrice(List<OrderItem> items) {
        return items.stream()
                .map(i->i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity()))).reduce(BigDecimal.valueOf(0), BigDecimal::add);
    }
}
