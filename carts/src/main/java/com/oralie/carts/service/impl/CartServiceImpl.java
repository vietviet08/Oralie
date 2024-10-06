package com.oralie.carts.service.impl;

import com.oralie.carts.model.Cart;
import com.oralie.carts.model.CartItem;
import com.oralie.carts.service.CartService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class CartServiceImpl implements CartService {


    @Override
    public Cart createCart() {
        return null;
    }

    @Override
    public Cart getCartById(Long id) {
        return null;
    }

    @Override
    public Cart updateCart(Long id, Cart cart) {
        return null;
    }

    @Override
    public void deleteCart(Long id) {

    }

    @Override
    public List<Cart> getAllCarts() {
        return null;
    }

    @Override
    public Set<CartItem> getCartItemByUserId(Long userId) {
        return null;
    }

    @Override
    public Cart addItemToCart(Long cartId, Long productId, Integer quantity) {
        return null;
    }

    @Override
    public Cart removeItemFromCart(Long cartId, Long productId) {
        return null;
    }

    @Override
    public Cart clearCart(Long cartId) {
        return null;
    }

    @Override
    public Cart checkoutCart(Long cartId) {
        return null;
    }
}
