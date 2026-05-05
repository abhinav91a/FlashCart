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
                .flashDeal(req.flashDeal() != null && req.flashDeal())
                .build();
        return ResponseEntity.ok(toResponse(service.create(p)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable Long id,
                                                  @RequestBody CreateProductRequest req) {
        Product p = Product.builder()
                .sku(req.sku())
                .name(req.name())
                .description(req.description())
                .priceInCents(req.priceInCents())
                .stock(req.stock())
                .flashDeal(req.flashDeal() != null && req.flashDeal())
                .build();
        return ResponseEntity.ok(toResponse(service.update(id, p)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/flash")
    public List<ProductResponse> flashDeals() {
        return service.getFlashDeals().stream()
                .map(this::toResponse)
                .toList();
    }

    private ProductResponse toResponse(Product p) {
        return new ProductResponse(
                p.getId(),
                p.getSku(),
                p.getName(),
                p.getDescription(),
                p.getPriceInCents(),
                p.getStock(),
                p.getFlashDeal()
        );
    }
}