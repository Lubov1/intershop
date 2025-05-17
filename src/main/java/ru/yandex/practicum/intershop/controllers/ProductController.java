package ru.yandex.practicum.intershop.controllers;

import javassist.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.intershop.dto.ProductDto;
import ru.yandex.practicum.intershop.services.ProductService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/main")
@AllArgsConstructor
public class ProductController {

    private ProductService productService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public String getProducts(Model model, @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "NO") String sort,
                              @RequestParam(defaultValue = "5") int pageSize,
                              @RequestParam(required = false) String search) {

        Page<ProductDto> pages = productService.getAllProducts(page, pageSize, sort, search);
        List<List<ProductDto>> rows = productService.convertToRows(pages);

        model.addAttribute("rows", rows);
        model.addAttribute("page", pages);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("sort", sort);
        return "main";
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/product/{id}")
    public String getProduct(@PathVariable long id, Model model) throws NotFoundException {
        ProductDto product = productService.getProductDto(id);
        model.addAttribute("product", product);

        return "item";
    }

    @ResponseStatus(HttpStatus.SEE_OTHER)
    @PostMapping(value ="/createItem", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String createPost(@RequestParam("name") String name
            ,@RequestParam("description") String description,
                             @RequestParam(value = "image", required = false) MultipartFile image,
                             @RequestParam("price") BigDecimal price
    ) throws IOException {

        productService.createItem(name, description, image, price);
        return "redirect:/main";
    }
}
