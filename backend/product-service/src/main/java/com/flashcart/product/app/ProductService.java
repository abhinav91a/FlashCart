package com.flashcart.product.app;

import com.flashcart.common.events.ProductIndexEvent;
import com.flashcart.product.domain.Product;
import com.flashcart.product.domain.ProductRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repo;
    private final KafkaTemplate<String, ProductIndexEvent> kafka;

    public ProductService(ProductRepository repo,
                          KafkaTemplate<String, ProductIndexEvent> kafka) {
        this.repo = repo;
        this.kafka = kafka;
    }

    public List<Product> getAll() {
        return repo.findAll();
    }

    public Product getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public List<Product> getFlashDeals() {
        return repo.findByFlashDealTrue();
    }

    public Product create(Product p) {
        Product saved = repo.save(p);
        publishIndexEvent(saved);
        return saved;
    }

    public Product update(Long id, Product updated) {
        Product existing = getById(id);
        existing.setSku(updated.getSku());
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setPriceInCents(updated.getPriceInCents());
        existing.setStock(updated.getStock());
        existing.setFlashDeal(updated.getFlashDeal());
        Product saved = repo.save(existing);
        publishIndexEvent(saved);
        return saved;
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    private void publishIndexEvent(Product p) {
        kafka.send("product-index", ProductIndexEvent.builder()
                .id(p.getId())
                .sku(p.getSku())
                .name(p.getName())
                .description(p.getDescription())
                .priceInCents(p.getPriceInCents())
                .stock(p.getStock())
                .flashDeal(p.getFlashDeal())
                .build());
    }
}