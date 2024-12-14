package com.oralie.products.repository;

import com.oralie.products.model.Category;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);

    Optional<Category> findByName(String name);

    List<Category> findAllByIdNot(Long id);

    List<Category> findByNameContainingIgnoreCase(String name);

    Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);

    List<Category> findAllByParentCategoryIsNull();

    @Query("SELECT c FROM Category c WHERE c.parentCategory.id = :idParent")
    List<Category> findAllByParentId(@Param("idParent") Long idParent);

    Optional<Category> findBySlug(String slug);
}
