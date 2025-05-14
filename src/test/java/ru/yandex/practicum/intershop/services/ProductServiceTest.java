package ru.yandex.practicum.intershop.services;

import javassist.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.intershop.dao.BasketItem;
import ru.yandex.practicum.intershop.dao.Product;
import ru.yandex.practicum.intershop.dto.ProductDto;
import ru.yandex.practicum.intershop.repositories.ProductRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ProductService.class})
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @MockitoBean
    private ProductRepository productRepository;



    @Test
    void getAllProducts() {
        Long productId = 1L;
        Product product = new Product(productId, "Первый продукт", "Описание", BigDecimal.ONE, null, new BasketItem(productId, 6,null));
        List<Product> products = List.of(product, product);
        Page<Product> page = new PageImpl<>(products);
        when(productRepository.findAll(any(Pageable.class))).thenReturn(page);

        assertEquals(2, productService.getAllProducts(0,2,"NO").getContent().size());
    }

    @Test
    void convertToRows() {
        List<ProductDto> products = new ArrayList<>();
        for (int i = 0; i < 22; i++) {
            products.add(new ProductDto(null, null, null,null, null, 0));
        }

        PageImpl<ProductDto> page = new PageImpl<>(products);

        List<List<ProductDto>> rows = productService.convertToRows(page);
        assertEquals(8, rows.size());
        assertEquals(1, rows.get(7).size());
    }

    @Test
    void getProductDto() throws NotFoundException {
        Long id = 1L;
        ProductDto productDto = new ProductDto(id, "Первый продукт", "Описание", BigDecimal.ONE, null, 6);
        Product product = new Product(id, "Первый продукт", "Описание", BigDecimal.ONE, null, new BasketItem(id, 6,null));
        when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(product));

        assertEquals(productService.getProductDto(id), productDto);
    }

    @Test
    void mapToDto() {
        Long id = 1L;
        Product product = new Product(id, "Первый продукт", "Описание", BigDecimal.ONE, null, new BasketItem(id, 6,null));

        ProductDto productDto = productService.mapToDto(product);
        assertEquals(product.getBasketItem().getQuantity(), productDto.quantity());
        assertEquals(product.getName(), productDto.name());

    }
}