package com.flashcart.product.api;

import com.flashcart.product.app.ProductService;
import com.flashcart.product.domain.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductResponse> list() {
        return service.getAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable Long id) {
        return toResponse(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@RequestBody CreateProductRequest req) {
        Product p = Product.builder()
                .sku(req.sku())
                .name(req.name())
                .description(req.description())
                .priceInCents(req.priceInCents())
                .stock(req.stock())
                .build();
        Product saved = service.create(p);
        return ResponseEntity.ok(toResponse(saved));
    }

    private ProductResponse toResponse(Product p) {
        return new ProductResponse(
                p.getId(),
                p.getSku(),
                p.getName(),
                p.getDescription(),
                p.getPriceInCents(),
                p.getStock()
        );
    }
}
