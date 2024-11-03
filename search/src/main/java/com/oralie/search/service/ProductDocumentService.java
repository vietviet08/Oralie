package com.oralie.search.service;

public interface ProductDocumentService {
    void saveProductDocument(Long productId);

    void updateProductDocument(Long productId);

    void deleteProductDocument(Long productId);
}
