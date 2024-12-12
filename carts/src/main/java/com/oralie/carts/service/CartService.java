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

    List<CartItemResponse> getCartItemByCartId(Long cartId);

    CartResponse updateCart(Long id, Cart cart);

    void deleteCart(Long id);

    CartResponse getCartByUserId(String userId);

    ListResponse<CartResponse> getAllCarts(int page, int size, String sortBy, String sort);

    List<CartItemResponse> getCartItemByUserId(String userId);

    CartResponse addItemToCart(String userId, Long productId, Long productOptionId, Integer quantity);

    CartResponse updateItemInCart(String userId, Long productId, Long itemId, Long productOptionId, Integer quantity);

    CartResponse removeItemFromCart(String userId, Long itemId);

    CartResponse clearCart(String userId);

    CartResponse checkoutCart(Long cartId);
}
