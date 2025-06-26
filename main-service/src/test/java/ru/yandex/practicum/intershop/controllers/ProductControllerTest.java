package ru.yandex.practicum.intershop.controllers;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import ru.yandex.practicum.intershop.dao.Product;
import ru.yandex.practicum.intershop.dto.CacheItemProduct;
import ru.yandex.practicum.intershop.services.ProductService;

import java.util.Arrays;
import java.util.List;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

class ProductControllerTest extends ControllerTest {

    @Autowired
    private ReactiveRedisTemplate<String, Product> redisTemplate;
    @Autowired
    private ReactiveRedisTemplate<String, CacheItemProduct[]> redisTemplateArray;
    @Autowired
    ProductService productService;

    @Value("${time}")
    private long time;
    @Test
    void getProduct() throws InterruptedException {
        Long productId = 5L;
        webTestClient
                .mutateWith(
                        SecurityMockServerConfigurers.mockAuthentication(
                                new UsernamePasswordAuthenticationToken(
                                        "user1", null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
                                )
                        ))
                .get().uri("/main/product/" + productId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("Пятый продукт"));
                    assertTrue(body.contains("Описание"));
                    assertTrue(body.contains("10.02"));
                    assertTrue(body.contains("<span>0</span>"));

                });
        Product product = redisTemplate
                .opsForValue()
                .get("Product:" + productId)
                .cast(Product.class)
                .block();
        assertNotNull(product);
        assertEquals(productId, product.getId());
        assertEquals("Пятый продукт", product.getName());

        sleep(time*1000);

        product = redisTemplate
                .opsForValue()
                .get("Product:" + productId)
                .cast(Product.class)
                .block();
        assertNull(product);

    }
    @Test
    void getProducts() throws InterruptedException {
        webTestClient
                .mutateWith(
                        SecurityMockServerConfigurers.mockAuthentication(
                                new UsernamePasswordAuthenticationToken(
                                        "user1", null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
                                )
                        ))
                .get().uri("/main")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("<form"));
                    assertTrue(body.contains("Первый продукт"));
                    assertTrue(body.contains("Второй продукт"));
                    assertTrue(body.contains("Третий продукт"));
                    assertTrue(body.contains("Четвертый продукт"));
                    assertTrue(body.contains("Пятый продукт"));
                    assertFalse(body.contains("Шестой продукт"));
                });
        List<CacheItemProduct> products = redisTemplateArray
                .opsForValue()
                .get("Products:1")
                .map(Arrays::asList)
                .block();
        assertNotNull(products);
        assertEquals(products.size(), 6);

        sleep(time*1000);

        products = redisTemplateArray
                .opsForValue()
                .get("Products:1")
                .map(Arrays::asList)
                .block();
        assertNull(products);
    }
}
