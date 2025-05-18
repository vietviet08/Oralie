package com.oralie.products.repository;

import com.oralie.products.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p " +
            "JOIN p.productCategories pc JOIN pc.category c WHERE c.name = :category" +
            " AND (p.name LIKE %:search% OR p.description LIKE %:search%)")
    Page<Product> findAllProducts(Pageable pageable,
                                  @Param("search") String search,
                                  @Param("category") String category
    );

    @Query("SELECT p FROM Product p " +
            "JOIN p.productCategories pc JOIN pc.category c " +
            "WHERE (:search IS NULL OR p.name LIKE %:search% OR p.description LIKE %:search%) " +
            "AND (:category IS NULL OR c.slug = :category) " +
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Product> findAllProductsWithFilter(Pageable pageable,
                                 @Param("search") String search,
                                 @Param("category") String category,
                                 @Param("minPrice") Double minPrice,
                                 @Param("maxPrice") Double maxPrice
    );

//    @Query(value = "SELECT p FROM Product p" +
//            " JOIN p.productCategories pc WHERE pc.category.name = :categoryName " +
//            "AND p.brand.name = :brandName")
//    Page<Product> findAllByBrandName(Pageable pageable,
//                                     @Param("categoryName") String categoryName,
//                                     @Param("brandName") String brandName);
//
//    @Query("SELECT p FROM Product p JOIN p.productCategories pc " +
//            "JOIN pc.category c WHERE c.name = :categoryName")
//    Page<Product> findAllByCategoryName(Pageable pageable, @Param("categoryName") String categoryName);

    @Query(value = "SELECT p FROM Product p" +
            " JOIN p.productCategories pc WHERE pc.category.slug = :categorySlug " +
            "AND p.brand.slug = :brandSlug")
    Page<Product> findAllByBrandAndCategorySlug(Pageable pageable,
                                     @Param("categorySlug") String categorySlug,
                                     @Param("brandSlug") String brandSlug);

    @Query(value = "SELECT p FROM Product p" +
            " JOIN p.productCategories pc " +
            "WHERE pc.category.slug = :categorySlug " +
            "AND p.brand.slug IN :brandSlugs " +
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Product> findAllByBrandsAndCategorySlugWithPriceRange(
            Pageable pageable,
            @Param("categorySlug") String categorySlug,
            @Param("brandSlugs") List<String> brandSlugs,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice);

    @Query(value = "SELECT p FROM Product p WHERE p.brand.slug = :brandSlug")
    Page<Product> findAllByBrandSlug(Pageable pageable, @Param("brandSlug") String brandSlug);

    @Query("SELECT p FROM Product p JOIN p.productCategories pc " +
            "JOIN pc.category c WHERE c.slug = :categorySlug")
    Page<Product> findAllByCategorySlug(Pageable pageable, @Param("categorySlug") String categorySlug);

    @Query("SELECT p FROM Product p JOIN p.productCategories pc " +
            "JOIN pc.category c " +
            "WHERE c.slug = :categorySlug " +
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Product> findAllByCategorySlugWithPriceRange(
            Pageable pageable, 
            @Param("categorySlug") String categorySlug,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice);

    @Query("SELECT p FROM Product p WHERE p.slug = :slug")
    Optional<Product> findBySlug(String slug);

    @Query("SELECT p FROM Product p JOIN p.productCategories pc " +
            "JOIN pc.category c WHERE p.slug = :slug AND c.name = :categoryName")
    Optional<Product> findBySlugs(String slug, String categoryName);

    @Query(value = "SELECT p.* FROM products p " +
            "JOIN product_categories pc ON p.product_id = pc.product_id " +
            "JOIN categories c ON pc.category_id = c.category_id " +
            "WHERE p.product_id != :id AND c.name = :categoryName " +
            "LIMIT 10", nativeQuery = true)
    List<Product> getTop10ProductRelatedByCategory(@Param("id") Long id, @Param("categoryName") String categoryName);

    boolean existsByName(String name);

    boolean existsBySlug(String slug);

    boolean existsById(Long productId);

    @Query(value = "SELECT p FROM Product p " +
            "JOIN p.productCategories pc " +
            "JOIN pc.category c " +
            "LEFT JOIN c.parentCategory pc2 " +
            "WHERE c.slug = :categorySlug OR pc2.slug = :categorySlug "
    )
    List<Product> getTop12ProductOutStandingByCategorySlug(@Param("categorySlug") String categorySlug);

}
