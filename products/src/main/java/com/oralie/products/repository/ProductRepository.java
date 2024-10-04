package com.oralie.products.repository;

import com.oralie.products.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = "SELECT p FROM Product p" +
                   " JOIN p.productCategories pc WHERE pc.category.name = :categoryName AND p.brand.name = :brandName")
    Page<Product> findAllByBrandName(Pageable pageable,
                                     @Param("categoryName") String categoryName,
                                     @Param("brandName") String brandName);

    @Query("SELECT p FROM Product p JOIN p.productCategories pc JOIN pc.category c WHERE c.name = :categoryName")
    Page<Product> findAllByCategoryName(Pageable pageable, @Param("categoryName") String categoryName);

    Optional<Product> findBySlug(String slug);

    @Query("SELECT p FROM Product p JOIN p.productCategories pc JOIN pc.category c WHERE p.slug = :slug AND c.name = :categoryName")
    Optional<Product> findBySlugs(String slug, String categoryName);


    boolean existsByName(String name);

    boolean existsBySlug(String slug);
}
