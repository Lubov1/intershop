package ru.yandex.practicum.intershop.services;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.intershop.dao.Product;
import ru.yandex.practicum.intershop.dto.ProductDto;
import ru.yandex.practicum.intershop.repositories.ProductRepository;
import javassist.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductService {

    private ProductRepository productRepository;
    private CartService cartService;

    @Transactional(readOnly = true)
    public Page<ProductDto> getAllProducts(int page, int size, String sort) {
        Page<Product> products = switch (sort) {
            case "ALPHA" -> productRepository.findAll(PageRequest.of(page, size, Sort.by("name")));
            case "PRICE" -> productRepository.findAll(PageRequest.of(page, size, Sort.by("price")));
            case "NO" -> productRepository.findAll(PageRequest.of(page, size));
            default -> throw new IllegalStateException("Unexpected value: " + sort);
        };

        return products.map(this::mapToDto);
    }

    public List<List<ProductDto>> convertToRows(Page<ProductDto> pages) {
        List<ProductDto> products = pages.getContent();
        List<List<ProductDto>> rows = new ArrayList<>();
        for (int i = 0; i < products.size(); i += 3) {
            int end = Math.min(i + 3, products.size());
            rows.add(products.subList(i, end));
        }
        return rows;
    }

    @Transactional(readOnly = true)
    public ProductDto getProductById(long id) throws NotFoundException {
        Optional<Product> product = productRepository.findById(id);
        if (!product.isPresent()) {
            throw new NotFoundException("product not found");
        }
        return mapToDto(product.get());
    }

    @Transactional(readOnly = true)
    int getQuantity(Product product) {
        return cartService.getQuantity(product);
    }

    ProductDto mapToDto(Product product) {
        return new ProductDto(product.getId(),
                product.getName(), product.getDescription(), product.getPrice(),
                product.getImage(), getQuantity(product));
    }
}
