package com.oralie.search.service.impl;


import com.oralie.search.dto.ProductParam;
import com.oralie.search.dto.response.ListResponse;
import com.oralie.search.model.ProductDocument;
import com.oralie.search.repository.ProductDocumentRepository;
import com.oralie.search.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import java.awt.print.Pageable;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductSearchImpl implements ProductSearchService {

    private final ProductDocumentRepository productDocumentRepository;

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public ListResponse<ProductDocument> searchProducts(ProductParam productParam) {

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if (productParam.getKeyword() != null && !productParam.getKeyword().isEmpty()) {
            boolQuery.must(org.elasticsearch.index.query.QueryBuilders.multiMatchQuery(productParam.getKeyword(), "name", "description"));
        }

        if (productParam.getBrand() != null && !productParam.getBrand().isEmpty()) {
            boolQuery.filter(QueryBuilders.termQuery("brand", productParam.getBrand()));
        }

        if (productParam.getCategory() != null && !productParam.getCategory().isEmpty()) {
            boolQuery.filter(QueryBuilders.termQuery("category", productParam.getCategory()));
        }

        if (productParam.getOption() != null && !productParam.getOption().isEmpty()) {
            boolQuery.filter(QueryBuilders.termQuery("option", productParam.getOption()));
        }

        if (productParam.getPriceFrom() != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("price").gte(productParam.getPriceFrom()));
        }

        if (productParam.getPriceTo() != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("price").lte(productParam.getPriceTo()));
        }


        Query searchQuery = new NativeQueryBuilder()
                .withQuery((Query) boolQuery)
                .withPageable(PageRequest.of(productParam.getPageNum(), productParam.getPageSize()))
                .build();

        SearchHits<ProductDocument> searchHits = elasticsearchOperations.search(searchQuery, ProductDocument.class);

        List<ProductDocument> products = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        ListResponse<ProductDocument> response = new ListResponse<>();
        response.setData(products);
        response.setPageNo(productParam.getPageNum());
        response.setPageSize(productParam.getPageSize());
        response.setTotalElements((int) searchHits.getTotalHits());
        response.setTotalPages((int) Math.ceil((double) searchHits.getTotalHits() / productParam.getPageSize()));
        response.setLast(productParam.getPageNum() == response.getTotalPages() - 1);

        return response;
    }


}
