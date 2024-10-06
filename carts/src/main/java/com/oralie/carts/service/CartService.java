package com.oralie.carts.service;

import com.oralie.carts.model.Cart;
import com.oralie.carts.model.CartItem;

import java.util.List;
import java.util.Set;

public interface CartService {
     Cart createCart();
     Cart getCartById(Long id);
     Cart updateCart(Long id, Cart cart);
     void deleteCart(Long id);
     List<Cart> getAllCarts();
     Set<CartItem> getCartItemByUserId(Long userId);
     Cart addItemToCart(Long cartId, Long productId, Integer quantity);
     Cart removeItemFromCart(Long cartId, Long productId);
     Cart clearCart(Long cartId);
     Cart checkoutCart(Long cartId);
}
