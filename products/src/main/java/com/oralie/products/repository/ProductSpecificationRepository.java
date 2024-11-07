package com.oralie.products.repository;

import com.oralie.products.model.ProductSpecification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSpecificationRepository extends JpaRepository<ProductSpecification, Long> {
    List<ProductSpecification> findAllByProductId(Long productId);

    ProductSpecification findByProductIdAndName(Long productId, String name);
}
