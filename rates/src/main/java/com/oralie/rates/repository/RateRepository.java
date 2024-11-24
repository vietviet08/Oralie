package com.oralie.rates.repository;

import com.oralie.rates.model.Rate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RateRepository extends JpaRepository<Rate, Long> {

    Page<Rate> findAllRates(Pageable pageable);

    @Query("SELECT r FROM Rate r WHERE r.productId = :productId")
    Page<Rate> findAllByProductId(Long productId, Pageable pageable);

    Optional<Rate> findByUserIdAndProductId(String userId, Long productId);
    
}
