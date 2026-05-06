package com.flashcart.search.app;

import com.flashcart.search.domain.ProductDocument;
import com.flashcart.search.domain.ProductSearchRepository;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Service;
import co.elastic.clients.elasticsearch._types.query_dsl.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private final ElasticsearchOperations esOps;
    private final ProductSearchRepository repo;

    public SearchService(ElasticsearchOperations esOps, ProductSearchRepository repo) {
        this.esOps = esOps;
        this.repo = repo;
    }

    public List<ProductDocument> search(String q) {
        var query = NativeQuery.builder()
                .withQuery(Query.of(qb -> qb
                        .bool(b -> b
                                .should(s -> s
                                        .multiMatch(mm -> mm
                                                .query(q)
                                                .fields("name^3", "description")
                                                .fuzziness("AUTO")
                                                .type(TextQueryType.BestFields)
                                        )
                                )
                                .should(s -> s
                                        .multiMatch(mm -> mm
                                                .query(q)
                                                .fields("name^3", "description")
                                                .type(TextQueryType.PhrasePrefix)  // ← handles "Ki" → "Kiwi"
                                        )
                                )
                        )
                ))
                .build();

        return esOps.search(query, ProductDocument.class)
                .stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    public void index(ProductDocument doc) {
        repo.save(doc);
    }

    public void delete(String id) {
        repo.deleteById(id);
    }
}