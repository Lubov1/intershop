package ru.yandex.practicum.intershop.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.intershop.dto.Order;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@Transactional
class OrderControllerTest extends ControllerTest {

    @Test
    void getOrders() throws Exception {
        var result = mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("orders"))
                .andExpect(model().attributeExists("orders"))
                .andReturn();

        List<Order> orders = ((List<Order>) Objects.requireNonNull(result.getModelAndView()).getModel().get("orders"));
        assertEquals(2, orders.size());
    }

    @Test
    void GetOrder() throws Exception {
        Long orderId = 1L;
        var result = mockMvc.perform(get("/orders/order/{orderId}",orderId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("order"))
                .andExpect(model().attributeExists("order"))
                .andReturn();

        Order order = ((Order) Objects.requireNonNull(result.getModelAndView()).getModel().get("order"));
        assertEquals(2, order.getItems().size());
        assertEquals(2, order.getItems().size());
        assertEquals(BigDecimal.valueOf(30.04), order.getPrice());
    }
}