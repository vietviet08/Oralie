package com.oralie.search.repository;

import com.oralie.search.model.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductDocumentRepository extends ElasticsearchRepository<ProductDocument, Long> {

    List<ProductDocument> findByProductNameContainingOrDescriptionContaining(String name, String description);

}
