package com.oralie.carts.service;

import com.oralie.carts.dto.response.CartItemResponse;
import com.oralie.carts.dto.response.CartResponse;
import com.oralie.carts.dto.response.ListResponse;
import com.oralie.carts.model.Cart;
import com.oralie.carts.model.CartItem;

import java.util.List;
import java.util.Set;

public interface CartService {
     CartResponse createCart(String userId);
     CartResponse getCartById(Long id);
     Set<CartItemResponse> getCartItemByCartId(Long cartId);
     CartResponse updateCart(Long id, Cart cart);
     void deleteCart(Long id);
     CartResponse getCartByUserId(String userId);
     ListResponse<CartResponse> getAllCarts(int page, int size, String sortBy, String sort);
     Set<CartItemResponse> getCartItemByUserId(String userId);
     CartResponse addItemToCart(String userId, Long productId, Integer quantity);
     CartResponse updateItemInCart(String userId, Long productId, Integer quantity);
     CartResponse removeItemFromCart(String userId, Long productId);
     CartResponse clearCart(String userId);
     CartResponse checkoutCart(Long cartId);
}
