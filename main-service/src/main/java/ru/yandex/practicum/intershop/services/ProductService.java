package ru.yandex.practicum.intershop.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.dao.BasketItem;
import ru.yandex.practicum.intershop.dao.Product;
import ru.yandex.practicum.intershop.dto.CacheItemProduct;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.dto.ProductDto;
import ru.yandex.practicum.intershop.exceptions.ProductNotFoundException;
import ru.yandex.practicum.intershop.repositories.CartRepository;
import ru.yandex.practicum.intershop.repositories.ProductRepository;

import java.time.Duration;
import java.util.*;

@Service
public class ProductService {
    public ProductService(ReactiveRedisTemplate<String, Product> redisTemplate, ReactiveRedisTemplate<String, CacheItemProduct[]> redisTemplateArray, ProductRepository productRepository, CartRepository cartRepository) {
        this.redisTemplate = redisTemplate;
        this.redisTemplateArray = redisTemplateArray;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
    }

    private final ReactiveRedisTemplate<String, Product> redisTemplate;
    private final ReactiveRedisTemplate<String, CacheItemProduct[]> redisTemplateArray;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    @Value("${time}")
    Integer time;

    @Transactional
    public Mono<Page<ProductDto>> getAllProducts(int page, int size, String sort, String search) {
        Flux<Product> sortedProducts = getProductsFromCache()
                .flatMapMany(Flux::fromIterable)
                .filter(product -> search==null || product.getName().contains(search))
                .sort(getCacheProductComparator(sort))
                .flatMap(cacheItem -> productRepository
                        .findById(cacheItem.getId())
                        .map(i->new Product(cacheItem, i.getImage())))
                .switchIfEmpty(productRepository
                        .findAll()
                        .collectList()
                        .flatMap(this::saveListProductsToCache)
                        .flatMapMany(Flux::fromIterable)
                        .filter(product -> search==null || product.getName().contains(search))
                        .sort(getProductComparator(sort)));

        PageRequest pageRequest = PageRequest.of(page, size);
        Mono<Map<Long, BasketItem>> basketItems = cartRepository.findAll().collectMap(BasketItem::getProductId);

        return Mono.zip(sortedProducts.collectList(), basketItems)
                .map(tuple -> {
                    List<Product> allProducts = tuple.getT1();
                    Map<Long, BasketItem> bItems = tuple.getT2();

                    int offset = page * size;

                    List<ProductDto> pageItems = allProducts.stream()
                            .skip(offset)
                            .limit(size)
                            .map(product -> {
                                BasketItem item = bItems.getOrDefault(product.getId(), new BasketItem(0L, 0));
                                return mapToDto(product, item);
                            })
                            .toList();

                    return new PageImpl<>(pageItems, pageRequest, allProducts.size());
                });
    }

    private Mono<List<Product>> saveListProductsToCache(List<Product> l) {
        CacheItemProduct[] toCache = l.stream()
                .map(CacheItemProduct::new)
                .toArray(CacheItemProduct[]::new);
        return redisTemplateArray.opsForValue()
                .set("Products:1", toCache, Duration.ofSeconds(time))
                .thenReturn(l);
    }

    public Comparator<CacheItemProduct> getCacheProductComparator(String sort) {
        return switch (sort) {
            case "ALPHA", "NO" -> (Comparator.comparing(CacheItemProduct::getName));
            case "PRICE" -> (Comparator.comparing(CacheItemProduct::getPrice));
            default -> throw new IllegalStateException("Unexpected value: " + sort);
        };
    }
    public Comparator<Product> getProductComparator(String sort) {
        return switch (sort) {
            case "ALPHA", "NO" -> (Comparator.comparing(Product::getName));
            case "PRICE" -> (Comparator.comparing(Product::getPrice));
            default -> throw new IllegalStateException("Unexpected value: " + sort);
        };
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
        return getProduct(id)
                .switchIfEmpty(Mono.error(new ProductNotFoundException("Product not found")))
                .flatMap(product -> cartRepository
                        .findByProductId(product.getId())
                        .defaultIfEmpty(new BasketItem(0L, 0))
                        .map(basketItem -> mapToDto(product, basketItem)))
                ;
    }

    private Mono<Product> saveProductToCache(Product product) {
        return redisTemplate.opsForValue()
                .set("Product:" + product.getId(), product, Duration.ofSeconds(time))
                .thenReturn(product);
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

    public Mono<Product> getProduct(Long id) {
        return redisTemplate.opsForValue()
                .get("Product:" + id)
                .cast(Product.class)
                .switchIfEmpty(productRepository
                        .findById(id)
                        .flatMap(this::saveProductToCache));
    }

    public Mono<List<CacheItemProduct>> getProductsFromCache() {
        return redisTemplateArray
                .opsForValue()
                .get("Products:1")
                .map(Arrays::asList);
    }
}

