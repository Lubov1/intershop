package ru.yandex.practicum.intershop.controllers;

import javassist.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.intershop.dto.ProductDto;
import ru.yandex.practicum.intershop.services.ProductService;

import java.util.List;

@Controller
@RequestMapping("/main")
@AllArgsConstructor
public class ProductController {

    private ProductService productService;

    @GetMapping
    public String getProducts(Model model, @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "NO") String sort,
                              @RequestParam(defaultValue = "5") int pageSize) {

        Page<ProductDto> pages = productService.getAllProducts(page, pageSize, sort);
        List<List<ProductDto>> rows = productService.convertToRows(pages);

        model.addAttribute("rows", rows);
        model.addAttribute("page", pages);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("sort", sort);
        return "main";
    }

    @GetMapping("/product/{id}")
    public String getProduct(@PathVariable long id, Model model) throws NotFoundException {
        ProductDto product = productService.getProductDto(id);
        model.addAttribute("product", product);

        return "item";
    }

}
