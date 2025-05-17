package ru.yandex.practicum.intershop.services;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.intershop.dao.Product;
import ru.yandex.practicum.intershop.dto.ProductDto;
import ru.yandex.practicum.intershop.repositories.ProductRepository;
import javassist.NotFoundException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductService {

    private ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<ProductDto> getAllProducts(int page, int size, String sort, String search) {
        PageRequest pageRequest = switch (sort) {
            case "ALPHA" -> PageRequest.of(page, size, Sort.by("name"));
            case "PRICE" -> PageRequest.of(page, size, Sort.by("price"));
            case "NO" -> PageRequest.of(page, size);
            default -> throw new IllegalStateException("Unexpected value: " + sort);
        };
        Page<Product> products = search==null ? productRepository.findAll(pageRequest)
                : productRepository.findAllByNameContaining(pageRequest, search);
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
    public ProductDto getProductDto(long id) throws NotFoundException {
        Optional<Product> product = productRepository.findById(id);
        if (!product.isPresent()) {
            throw new NotFoundException("product not found");
        }
        return mapToDto(product.get());
    }

    ProductDto mapToDto(Product product) {
        int quantity = product.getBasketItem() == null ? 0 : product.getBasketItem().getQuantity();
        String image = null;
        if (product.getImage() != null) {
            image = Base64.getEncoder().encodeToString(product.getImage());
        }
        return new ProductDto(product.getId(),
                product.getName(), product.getDescription(), product.getPrice(),
                image, quantity);
    }

    @Transactional
    public void createItem(String name, String description, MultipartFile image, BigDecimal price) throws IOException {
        productRepository.save(new Product(name, description, price, imageConverter(image), null));
    }

    public byte[] imageConverter(MultipartFile image) throws IOException {
        byte[] im = new byte[0];
        if (image != null && !image.isEmpty()) {
            im = image.getBytes();
        }
        return im;
    }
}

