package com.oralie.inventory.repository;

import com.oralie.inventory.model.WareHouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WareHouseRepository extends JpaRepository<WareHouse, Long> {
    Page<WareHouse> findByNameContainingIgnoreCase(String search, Pageable pageable);
}
