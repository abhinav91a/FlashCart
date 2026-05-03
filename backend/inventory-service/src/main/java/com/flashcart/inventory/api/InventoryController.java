package com.flashcart.inventory.api;

import com.flashcart.inventory.domain.InventoryItem;
import com.flashcart.inventory.domain.InventoryRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryRepository repo;
    private final StringRedisTemplate redis;

    public InventoryController(InventoryRepository repo, StringRedisTemplate redis) {
        this.repo = repo;
        this.redis = redis;
    }

    @GetMapping
    public List<InventoryItem> list() {
        return repo.findAll();
    }

    @PostMapping
    public InventoryItem create(@RequestBody InventoryItem item) {
        InventoryItem saved = repo.save(item);
        redis.opsForValue().set("product:" + saved.getProductId() + ":stock",
                String.valueOf(saved.getStock()));
        return saved;
    }

    @GetMapping("/{productId}")
    public InventoryItem get(@PathVariable Long productId) {
        return repo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }
}