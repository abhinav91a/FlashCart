package com.flashcart.search.api;

import com.flashcart.search.app.SearchService;
import com.flashcart.search.domain.ProductDocument;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService service;

    public SearchController(SearchService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductDocument> search(@RequestParam String q) {
        return service.search(q);
    }
}