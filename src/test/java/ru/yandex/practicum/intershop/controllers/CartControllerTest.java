package ru.yandex.practicum.intershop.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.intershop.dto.Item;
import ru.yandex.practicum.intershop.dto.Order;
import ru.yandex.practicum.intershop.dto.ProductDto;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@Transactional
class CartControllerTest extends ControllerTest {

    @Test
    void getCart() throws Exception {
        var result = mockMvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("items"))
                .andReturn();

        List<Item> items = ((List<Item>) Objects.requireNonNull(result.getModelAndView()).getModel().get("items"));
        assertEquals(3, items.size());
    }

    @Test
    void addToCartPlus() throws Exception {
        Long productId = 1L;
        String action = "plus";

        mockMvc.perform(post("/cart/update")
                        .param("productId", String.valueOf(productId))
                        .param("action", action))
                .andExpect(status().isSeeOther())
                .andReturn();

        var result = mockMvc.perform(get("/main/product/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("item"))
                .andExpect(model().attributeExists("product"))
                .andReturn();

        ProductDto product = ((ProductDto) Objects.requireNonNull(result.getModelAndView()).getModel().get("product"));
        assertEquals(3, product.quantity());
    }

    @Test
    void addToCartMinus() throws Exception {
        Long productId = 1L;
        String action = "minus";

        mockMvc.perform(post("/cart/update")
                        .param("productId", String.valueOf(productId))
                        .param("action", action))
                .andExpect(status().isSeeOther());

        var result = mockMvc.perform(get("/main/product/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("item"))
                .andExpect(model().attributeExists("product"))
                .andReturn();

        ProductDto product = ((ProductDto) Objects.requireNonNull(result.getModelAndView()).getModel().get("product"));
        assertEquals(1, product.quantity());
    }

    @Test
    void addToCart() throws Exception {
        Long productId = 1L;
        String action = "not exist";

        mockMvc.perform(post("/cart/update")
                        .param("productId", String.valueOf(productId))
                        .param("action", action))
                .andExpect(status().isNotFound());
    }

    @Test
    void buy() throws Exception {

        var result1 = mockMvc.perform(post("/cart/buy"))
                .andExpect(status().isSeeOther()).andReturn();
        String id = Arrays.stream(result1.getResponse().getRedirectedUrl().split("/")).toList().get(3);
        var result = mockMvc.perform(get("/orders/order/{orderId}",Long.parseLong(id)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("order"))
                .andExpect(model().attributeExists("order"))
                .andReturn();

        Order order = ((Order) Objects.requireNonNull(result.getModelAndView()).getModel().get("order"));
        assertEquals(3, order.getItems().size());

        var result3 = mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("orders"))
                .andExpect(model().attributeExists("orders"))
                .andReturn();

        List<Order> orders = ((List<Order>) Objects.requireNonNull(result3.getModelAndView()).getModel().get("orders"));
        assertEquals(3, orders.size());
    }
}