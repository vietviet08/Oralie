package com.oralie.carts.service;

import com.oralie.carts.dto.response.CartItemResponse;
import com.oralie.carts.dto.response.CartResponse;
import com.oralie.carts.dto.response.ListResponse;
import com.oralie.carts.model.Cart;
import com.oralie.carts.model.CartItem;

import java.util.List;
import java.util.Set;

public interface CartService {
     CartResponse createCart(Long userId);
     CartResponse getCartById(Long id);
     CartResponse updateCart(Long id, Cart cart);
     void deleteCart(Long id);
     ListResponse<CartResponse> getAllCarts(int page, int size, String sortBy, String sort);
     Set<CartItemResponse> getCartItemByUserId(Long userId);
     Set<CartItemResponse> getCartItemByCartId(Long cartId);
     CartResponse addItemToCart(Long cartId, Long productId, Integer quantity);
     CartResponse removeItemFromCart(Long cartId, Long productId);
     CartResponse clearCart(Long cartId);
     CartResponse checkoutCart(Long cartId);
}
