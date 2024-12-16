package com.oralie.search.service.impl;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import com.oralie.search.constant.ProductField;
import com.oralie.search.dto.ProductParam;
import com.oralie.search.dto.response.ListResponse;
import com.oralie.search.model.ProductDocument;
import com.oralie.search.repository.ProductDocumentRepository;
import com.oralie.search.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.common.unit.Fuzziness;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductSearchImpl implements ProductSearchService {

//    private final ProductDocumentRepository productDocumentRepository;

    private final ElasticsearchOperations elasticsearchOperations;

    public ListResponse<ProductDocument> searchProducts(ProductParam productParam) {
        NativeQueryBuilder nativeQuery = NativeQuery.builder()
                .withAggregation("categories", Aggregation.of(a -> a
                        .terms(ta -> ta.field(ProductField.CATEGORIES))))
                .withAggregation("attributes", Aggregation.of(a -> a
                        .terms(ta -> ta.field(ProductField.ATTRIBUTES))))
                .withAggregation("brands", Aggregation.of(a -> a
                        .terms(ta -> ta.field(ProductField.BRAND))))
                .withQuery(q -> q
                        .bool(b -> b
                                .should(s -> s
                                        .multiMatch(m -> m
                                                .fields(ProductField.NAME, ProductField.BRAND, ProductField.CATEGORIES)
                                                .query(productParam.getKeyword())
                                                .fuzziness(Fuzziness.ONE.asString())
                                        )
                                )
                        )
                )
                .withPageable(PageRequest.of(productParam.getPageNum(), productParam.getPageSize()));


        nativeQuery.withFilter(f -> f
                .bool(b -> {
                    extractedTermsFilter(productParam.getBrand(), ProductField.BRAND, b);
                    extractedTermsFilter(productParam.getCategory(), ProductField.CATEGORIES, b);
                    extractedRange(productParam.getPriceFrom(), productParam.getPriceTo(), b);
                    b.must(m -> m.term(t -> t.field(ProductField.IS_PUBLISHED).value(true)));
                    return b;
                })
        );

        if (Objects.equals(productParam.getSortType(), "asc")) {
            nativeQuery.withSort(Sort.by(Sort.Direction.ASC, ProductField.PRICE));
        } else if (Objects.equals(productParam.getSortType(), "desc")) {
            nativeQuery.withSort(Sort.by(Sort.Direction.DESC, ProductField.PRICE));
        } else {
            nativeQuery.withSort(Sort.by(Sort.Direction.DESC, ProductField.CREATE_ON));
        }

        SearchHits<ProductDocument> searchHitsResult = elasticsearchOperations.search(nativeQuery.build(), ProductDocument.class);
        SearchPage<ProductDocument> productPage = SearchHitSupport.searchPageFor(searchHitsResult, nativeQuery.getPageable());

        List<ProductDocument> products = searchHitsResult.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        ListResponse<ProductDocument> response = new ListResponse<>();
        response.setData(products);
        response.setPageNo(productParam.getPageNum());
        response.setPageSize(productParam.getPageSize());
        response.setTotalElements((int) searchHitsResult.getTotalHits());
        response.setTotalPages((int) Math.ceil((double) searchHitsResult.getTotalHits() / productParam.getPageSize()));
        response.setLast(productParam.getPageNum() == response.getTotalPages() - 1);

        return response;
    }

    private void extractedTermsFilter(String fieldValues, String productField, BoolQuery.Builder b) {
        if (StringUtils.isBlank(fieldValues)) {
            return;
        }
        String[] valuesArray = fieldValues.split(",");
        b.must(m -> {
            BoolQuery.Builder innerBool = new BoolQuery.Builder();
            for (String value : valuesArray) {
                innerBool.should(s -> s
                        .term(t -> t
                                .field(productField)
                                .value(value)
                                .caseInsensitive(true)
                        )
                );
            }
            return new Query.Builder().bool(innerBool.build());
        });
    }

    private void extractedRange(Number min, Number max, BoolQuery.Builder bool) {
        if (min != null || max != null) {
            bool.must(m -> m
                    .range(r -> r
                            .field(ProductField.PRICE)
                            .from(min != null ? min.toString() : null)
                            .to(max != null ? max.toString() : null)
                    )
            );
        }
    }

    private Map<String, Map<String, Long>> getAggregations(SearchHits<ProductDocument> searchHits) {
        List<org.springframework.data.elasticsearch.client.elc.Aggregation> aggregations = new ArrayList<>();
        if (searchHits.hasAggregations()) {
            ((List<ElasticsearchAggregation>) searchHits.getAggregations().aggregations()) //NOSONAR
                    .forEach(elsAgg -> aggregations.add(elsAgg.aggregation()));
        }

        Map<String, Map<String, Long>> aggregationsMap = new HashMap<>();
        aggregations.forEach(agg -> {
            Map<String, Long> aggregation = new HashMap<>();
            StringTermsAggregate stringTermsAggregate = (StringTermsAggregate) agg.getAggregate()._get();
            List<StringTermsBucket> stringTermsBuckets
                    = (List<StringTermsBucket>) stringTermsAggregate.buckets()._get();
            stringTermsBuckets.forEach(bucket -> aggregation.put(bucket.key()._get().toString(), bucket.docCount()));
            aggregationsMap.put(agg.getName(), aggregation);
        });

        return aggregationsMap;
    }


    @Override
    public List<String> autoCompleteProductName(String keyword) {
        NativeQuery matchQuery = NativeQuery.builder()
                .withQuery(
                        q -> q.matchPhrasePrefix(
                                matchPhrasePrefix -> matchPhrasePrefix.field("name").query(keyword)
                        )
                )
                .withSourceFilter(new FetchSourceFilter(
                        new String[]{"name"},
                        null)
                )
                .build();
        SearchHits<ProductDocument> result = elasticsearchOperations.search(matchQuery, ProductDocument.class);
        List<ProductDocument> products = result.stream().map(SearchHit::getContent).toList();

        return products.stream().map(ProductDocument::getProductName).collect(Collectors.toList());
    }


}
