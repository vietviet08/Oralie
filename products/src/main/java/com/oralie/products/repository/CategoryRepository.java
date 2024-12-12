package com.oralie.products.repository;

import com.oralie.products.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);

    Category findByName(String name);

    List<Category> findAllByIdNot(Long id);

    List<Category> findByNameContainingIgnoreCase(String name);

}
