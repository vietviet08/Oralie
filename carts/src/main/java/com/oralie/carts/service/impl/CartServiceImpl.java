package com.oralie.carts.service.impl;

import com.oralie.carts.dto.ProductResponse;
import com.oralie.carts.dto.response.CartItemResponse;
import com.oralie.carts.dto.response.CartResponse;
import com.oralie.carts.dto.response.ListResponse;
import com.oralie.carts.exception.ResourceNotFoundException;
import com.oralie.carts.model.Cart;
import com.oralie.carts.model.CartItem;
import com.oralie.carts.repository.CartRepository;
import com.oralie.carts.repository.client.product.ProductFeignClient;
import com.oralie.carts.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    private final ProductFeignClient productFeignClient;

    @Override
    public CartResponse createCart(Long userId) {
        boolean isCartExist = cartRepository.existsByUserId(userId);
        Cart cart;
        if (!isCartExist) {
            cart = Cart.builder()
                    .userId(userId)
                    .quantity(0)
                    .totalPrice(0.0)
                    .cartItems(Set.of())
                    .build();
            Cart cartSaved = cartRepository.save(cart);
            return mapToCartResponse(cartSaved);
        }
        return null;
    }

    @Override
    public CartResponse getCartById(Long id) {
        Cart cart = cartRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cart", "id", id + ""));
        return mapToCartResponse(cart);
    }

    @Override
    public CartResponse updateCart(Long id, Cart cart) {
        Cart cartToUpdate = cartRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cart", "id", id + ""));
        cartToUpdate.setUserId(cart.getUserId());
        cartToUpdate.setQuantity(cart.getQuantity());
        cartToUpdate.setTotalPrice(cart.getTotalPrice());
        Cart cartUpdated = cartRepository.save(cartToUpdate);
        return mapToCartResponse(cartUpdated);
    }

    @Override
    public void deleteCart(Long id) {
        Cart cart = cartRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cart", "id", id + ""));
        cartRepository.delete(cart);
    }

    @Override
    public ListResponse<CartResponse> getAllCarts(int page, int size, String sortBy, String sort) {
        Sort sortObj = sort.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<Cart> pageCarts = cartRepository.findAll(pageable);
        List<Cart> carts = pageCarts.getContent();

        return ListResponse.<CartResponse>builder()
                .data(maptoCartResponseList(carts))
                .pageNo(pageCarts.getNumber())
                .pageSize(pageCarts.getSize())
                .totalElements((int) pageCarts.getTotalElements())
                .totalPages(pageCarts.getTotalPages())
                .isLast(pageCarts.isLast())
                .build();
    }

    @Override
    public Set<CartItemResponse> getCartItemByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", userId + ""));
        Set<CartItem> cartItems = cart.getCartItems();
        return mapToCartItemResponseSet(cartItems);
    }

    @Override
    public Set<CartItemResponse> getCartItemByCartId(Long cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", "id", cartId + ""));
        Set<CartItem> cartItems = cart.getCartItems();
        return mapToCartItemResponseSet(cartItems);
    }

    @Override
    public CartResponse addItemToCart(Long cartId, Long productId, Integer quantity) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", "id", cartId + ""));

        Set<CartItem> cartItems = cart.getCartItems();

        ProductResponse product = productFeignClient.getProductById(productId).getBody();

        //need check cart null or not

        if (product == null) {
            throw new ResourceNotFoundException("Product", "id", productId + "");
        }
        if (cartItems == null) {
            cartItems = new HashSet<>();
            CartItem cartItem = CartItem.builder()
                    .productId(productId)
                    .quantity(quantity)
                    .price(product.getPrice())
                    .totalPrice(product.getPrice() * quantity)
                    .cart(cart)
                    .build();
            cartItems.add(cartItem);
            cart.setCartItems(cartItems);
            cart.setQuantity(cart.getQuantity() + quantity);
            cart.setTotalPrice(cart.getTotalPrice() + product.getPrice() * quantity);
            Cart cartSaved = cartRepository.save(cart);
            return mapToCartResponse(cartSaved);
        } else {
            CartItem cartItem = cartItems.stream()
                    .filter(item -> item.getProductId().equals(productId))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("CartItem", "productId", productId + " check cart if not null and exist productId"));
            if (cartItem == null) {
                cartItem = CartItem.builder()
                        .productId(productId)
                        .quantity(quantity)
                        .price(product.getPrice())
                        .totalPrice(product.getPrice() * quantity)
                        .cart(cart)
                        .build();
                cartItems.add(cartItem);
                cart.setCartItems(cartItems);
                cart.setQuantity(cart.getQuantity() + quantity);
                cart.setTotalPrice(cart.getTotalPrice() + product.getPrice() * quantity);
                Cart cartSaved = cartRepository.save(cart);
                return mapToCartResponse(cartSaved);
            } else {
                cartItem.setQuantity(cartItem.getQuantity() + quantity);
                cartItem.setTotalPrice(cartItem.getTotalPrice() + product.getPrice() * quantity);
                cart.setQuantity(cart.getQuantity() + quantity);
                cart.setTotalPrice(cart.getTotalPrice() + product.getPrice() * quantity);
                Cart cartSaved = cartRepository.save(cart);
                return mapToCartResponse(cartSaved);
            }
        }
    }

    @Override
    public CartResponse removeItemFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", "id", cartId + ""));
        Set<CartItem> cartItems = cart.getCartItems();
        if (cartItems == null) {
            throw new ResourceNotFoundException("CartItem", "productId", productId + " check cart if not null and exist productId");
        }
        CartItem cartItem = cartItems.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "productId", productId + " check cart if not null and exist productId"));
        if (cartItem == null) {
            throw new ResourceNotFoundException("CartItem", "productId", productId + " check cart if not null and exist productId");
        } else {
            cartItems.remove(cartItem);
            cart.setCartItems(cartItems);
            cart.setQuantity(cart.getQuantity() - cartItem.getQuantity());
            cart.setTotalPrice(cart.getTotalPrice() - cartItem.getTotalPrice());
            Cart cartSaved = cartRepository.save(cart);
            return mapToCartResponse(cartSaved);
        }
    }

    @Override
    public CartResponse clearCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", "id", cartId + ""));
        cart.setCartItems(new HashSet<>());
        cart.setQuantity(0);
        cart.setTotalPrice(0.0);
        Cart cartSaved = cartRepository.save(cart);
        return mapToCartResponse(cartSaved);
    }

    @Override
    public CartResponse checkoutCart(Long cartId) {
        return null;
    }

    private CartResponse mapToCartResponse(Cart cart) {
        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .quantity(cart.getQuantity())
                .totalPrice(cart.getTotalPrice())
                .build();
    }

    private List<CartResponse> maptoCartResponseList(List<Cart> carts) {
        return carts.stream()
                .map(this::mapToCartResponse)
                .collect(Collectors.toList());
    }

    private CartItemResponse mapToCartItemResponse(CartItem cartItem) {
        return CartItemResponse.builder()
                .id(cartItem.getId())
                .productId(cartItem.getProductId())
                .quantity(cartItem.getQuantity())
                .price(cartItem.getPrice())
                .totalPrice(cartItem.getTotalPrice())
                .build();
    }

    private Set<CartItemResponse> mapToCartItemResponseSet(Set<CartItem> cartItems) {
        return cartItems.stream()
                .map(this::mapToCartItemResponse)
                .collect(Collectors.toSet());
    }
}
