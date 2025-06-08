package ru.yandex.practicum.intershop.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.services.ProductService;


@Controller
@RequestMapping("/main")
@AllArgsConstructor
public class ProductController {

    private ProductService productService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Mono<String> getProducts(Model model, @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "NO") String sort,
                                    @RequestParam(defaultValue = "5") int pageSize,
                                    @RequestParam(required = false) String search) {
        return productService.getAllProducts(page, pageSize, sort, search)
                .doOnNext(products-> {
                    model.addAttribute("rows", productService.convertToRows(products));
                    model.addAttribute("page", products);
                    model.addAttribute("pageSize", pageSize);
                    model.addAttribute("sort", sort);
                })
                .map(productDtos -> "main");
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/product/{id}")
    public Mono<String> getProduct(@PathVariable long id, Model model) {
        return productService.getProductDto(id)
                .doOnNext(product -> model.addAttribute("product", product))
                .map(productDto -> "item");
    }

    @PostMapping(value ="/createItem", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> createItem(@ModelAttribute ItemDto itemDto) {
        return productService.createItem(itemDto)
                .then(Mono.fromCallable(() -> "redirect:/main"));
    }
}
