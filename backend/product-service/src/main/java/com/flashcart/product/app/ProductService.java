package com.flashcart.product.app;

import com.flashcart.product.domain.Product;
import com.flashcart.product.domain.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public List<Product> getAll() {
        return repo.findAll();
    }

    public Product getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Product create(Product p) {
        return repo.save(p);
    }

    public List<Product> getFlashDeals() {
        return repo.findByFlashDealTrue();
    }

    public Product update(Long id, Product updated) {
        Product existing = getById(id);
        existing.setSku(updated.getSku());
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setPriceInCents(updated.getPriceInCents());
        existing.setStock(updated.getStock());
        existing.setFlashDeal(updated.getFlashDeal());
        return repo.save(existing);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}