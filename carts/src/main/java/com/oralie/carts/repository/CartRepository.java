package com.oralie.carts.repository;

import com.oralie.carts.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    boolean existsByUserId(String userId);

    Optional<Cart> findByUserId(String userId);

//    Cart getCartByUserId(String userId);
}
