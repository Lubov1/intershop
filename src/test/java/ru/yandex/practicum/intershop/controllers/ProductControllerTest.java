package ru.yandex.practicum.intershop.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.intershop.dto.ProductDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@Transactional
class ProductControllerTest extends ControllerTest {

    @Test
    void getProducts() throws Exception {
        var result = mockMvc.perform(get("/main"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("rows"))
                .andReturn();

        List<List<ProductDto>> products = ((List<List<ProductDto>>) Objects.requireNonNull(result.getModelAndView()).getModel().get("rows"));
        assertEquals(2, products.size());
    }

    @Test
    void getProduct() throws Exception {
        Long id = 1L;
        var result = mockMvc.perform(get("/main/product/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("item"))
                .andExpect(model().attributeExists("product"))
                .andReturn();

        ProductDto product = ((ProductDto) Objects.requireNonNull(result.getModelAndView()).getModel().get("product"));
        assertEquals(new ProductDto(id, "Первый продукт", "Описание", BigDecimal.valueOf(10.02), null, 2), product);
    }
}