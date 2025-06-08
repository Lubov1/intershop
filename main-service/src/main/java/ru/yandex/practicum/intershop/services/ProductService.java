package ru.yandex.practicum.intershop.services;

import lombok.AllArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.dao.BasketItem;
import ru.yandex.practicum.intershop.dao.Product;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.dto.ProductDto;
import ru.yandex.practicum.intershop.exceptions.ProductNotFoundException;
import ru.yandex.practicum.intershop.repositories.CartRepository;
import ru.yandex.practicum.intershop.repositories.ProductRepository;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class ProductService {

    private ProductRepository productRepository;
    private CartRepository cartRepository;

    @Transactional
    public Mono<Page<ProductDto>> getAllProducts(int page, int size, String sort, String search) {
        PageRequest pageRequest = switch (sort) {
            case "ALPHA" -> PageRequest.of(page, size, Sort.by("name"));
            case "PRICE" -> PageRequest.of(page, size, Sort.by("price"));
            case "NO" -> PageRequest.of(page, size);
            default -> throw new IllegalStateException("Unexpected value: " + sort);
        };
        Flux<Product> productDtoFlux = search!=null? productRepository.searchAllByNameContaining(search, pageRequest): productRepository.findAllBy(pageRequest);
        Mono<Map<Long, BasketItem>> basketItems = cartRepository.findAll().collectMap(BasketItem::getProductId);

        return basketItems.flatMapMany(basketItems1-> productDtoFlux.map(product -> {
            BasketItem basketItem = basketItems1.getOrDefault(product.getId(), new BasketItem(0L, 0));
            return mapToDto(product, basketItem);
        })).collectList()
                .zipWith(search != null
                        ? productRepository.countByNameContaining(search)
                        : productRepository.count())
                .map(tuple-> new PageImpl<>(tuple.getT1(), pageRequest, tuple.getT2()));

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

    @Transactional
    public Mono<ProductDto> getProductDto(long id) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new ProductNotFoundException("Product not found")))
                .flatMap(product -> cartRepository
                        .findByProductId(product.getId())
                        .defaultIfEmpty(new BasketItem(0L, 0))
                        .map(basketItem -> mapToDto(product, basketItem)));
    }

    ProductDto mapToDto(Product product, BasketItem basketItem) {
        int quantity = basketItem == null ? 0 : basketItem.getQuantity();
        String image = null;
        if (product.getImage() != null) {
            image = Base64.getEncoder().encodeToString(product.getImage());
        }
        return new ProductDto(product.getId(),
                product.getName(), product.getDescription(), product.getPrice(),
                image, quantity);
    }

    @Transactional
    public Mono<Void> createItem(ItemDto itemDto) {
        Mono<DataBuffer> dataBufferMono = itemDto.getImage() == null
                ? Mono.empty()
                : DataBufferUtils.join(itemDto.getImage().content());

        return dataBufferMono
                .defaultIfEmpty(new DefaultDataBufferFactory().wrap(new byte[0])).map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return bytes;
                })
                .flatMap(image -> {
                    Product product = new Product(itemDto.getName(), itemDto.getDescription(), itemDto.getPrice(), image, null);
                    return productRepository.save(product);
                }).then();
    }
}

