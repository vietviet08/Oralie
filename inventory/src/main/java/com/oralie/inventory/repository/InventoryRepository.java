package com.oralie.inventory.repository;

import com.oralie.inventory.model.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Query("SELECT i FROM Inventory i WHERE i.wareHouse.id = :warehouseId AND i.productId = :productId")
    boolean existsByWarehouseIdAndProductId(Long warehouseId, Long productId);

    Optional<Inventory> findByProductId(Long productId);

    Page<Inventory> findByProductNameContainingIgnoreCase(String search, Pageable pageable);
}
