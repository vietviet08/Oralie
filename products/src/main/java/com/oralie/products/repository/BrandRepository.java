package com.oralie.products.repository;

import com.oralie.products.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    boolean existsByName(String name);
    boolean existsByIdAndProductsIsEmpty(Long id);
}
