package com.oralie.rates.repository;

import com.oralie.rates.model.Rate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RateRepository extends JpaRepository<Long, Rate> {

    Page<Rate> findAllRates(Pageable pageable);
}
