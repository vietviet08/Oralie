package com.oralie.carts.service.impl;

import com.oralie.carts.dto.response.CartItemResponse;
import com.oralie.carts.dto.response.CartResponse;
import com.oralie.carts.dto.response.ListResponse;
import com.oralie.carts.dto.client.search.ProductBaseResponse;
import com.oralie.carts.dto.client.products.ProductOptionResponse;
import com.oralie.carts.exception.ResourceNotFoundException;
import com.oralie.carts.model.Cart;
import com.oralie.carts.model.CartItem;
import com.oralie.carts.repository.CartItemRepository;
import com.oralie.carts.repository.CartRepository;
import com.oralie.carts.service.CartService;
import com.oralie.carts.service.ProductService;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;

    private final ProductService productService;

    @Override
    public CartResponse createCart(String userId) {
        boolean isCartExist = cartRepository.existsByUserId(userId);
        Cart cart;
        if (!isCartExist) {
            cart = Cart.builder()
                    .userId(userId)
                    .quantity(0)
                    .totalPrice(0.0)
                    .cartItems(new ArrayList<>())
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
    public CartResponse getCartByUserId(String userId) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", userId));
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
                .data(mapToCartResponseList(carts))
                .pageNo(pageCarts.getNumber())
                .pageSize(pageCarts.getSize())
                .totalElements((int) pageCarts.getTotalElements())
                .totalPages(pageCarts.getTotalPages())
                .isLast(pageCarts.isLast())
                .build();
    }

    @Override
    public List<CartItemResponse> getCartItemByUserId(String userId) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", userId + ""));
        List<CartItem> cartItems = cart.getCartItems();
        return mapToCartItemResponseList(cartItems);
    }

    @Override
    public List<CartItemResponse> getCartItemByCartId(Long cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", "id", cartId + ""));
        List<CartItem> cartItems = cart.getCartItems();
        return mapToCartItemResponseList(cartItems);
    }

    @Override
    public CartResponse addItemToCart(String userId, Long productId, Long productOptionId, Integer quantity) {

//        Cart cart = cartRepository.findByUserId(userId).orElse(null);
//
//        if (cart == null) {
//            cart = Cart.builder()
//                    .userId(userId)
//                    .quantity(0)
//                    .totalPrice(0.0)
//                    .cartItems(new ArrayList<>())
//                    .build();
//        }
//
//        Set<CartItem> cartItems = cart.getCartItems();
//
//        ProductBaseResponse product = productService.getProductById(productId);
//
//        if (product == null)
//            throw new ResourceNotFoundException("Product", "id", productId + "");
//        if (cartItems == null || cartItems.isEmpty()) {
//            cartItems = new ArrayList<>();
//            CartItem cartItem = CartItem.builder()
//                    .productId(productId)
//                    .productName(product.getName())
//                    .productSlug(product.getSlug())
//                    .urlImageThumbnail(product.getImage())
//                    .quantity(quantity)
//                    .price(product.getPrice())
//                    .totalPrice(product.getPrice() * quantity)
//                    .cart(cart)
//                    .build();
//            cartItems.add(cartItem);
//            cart.setCartItems(cartItems);
//            cart.setQuantity(cart.getQuantity() + quantity);
//            cart.setTotalPrice(cart.getTotalPrice() + product.getPrice() * quantity);
//            Cart cartSaved = cartRepository.save(cart);
//            return mapToCartResponse(cartSaved);
//        } else {
//            CartItem cartItem = cartItems.stream()
//                    .filter(item -> item.getProductId().equals(productId))
//                    .findFirst()
//                    .orElse(null);
//            if (cartItem == null) {
//                cartItem = CartItem.builder()
//                        .productId(productId)
//                        .productName(product.getName())
//                        .urlImageThumbnail(product.getImage())
//                        .productSlug(product.getSlug())
//                        .quantity(quantity)
//                        .price(product.getPrice())
//                        .totalPrice(product.getPrice() * quantity)
//                        .cart(cart)
//                        .build();
//                cartItems.add(cartItem);
//                cart.setCartItems(cartItems);
//                cart.setQuantity(cart.getQuantity() + quantity);
//                cart.setTotalPrice(cart.getTotalPrice() + product.getPrice() * quantity);
//                Cart cartSaved = cartRepository.save(cart);
//                return mapToCartResponse(cartSaved);
//            } else {
//                cartItem.setQuantity(cartItem.getQuantity() + quantity);
//                cartItem.setTotalPrice(cartItem.getTotalPrice() + product.getPrice() * quantity);
//                cart.setQuantity(cart.getQuantity() + quantity);
//                cart.setTotalPrice(cart.getTotalPrice() + product.getPrice() * quantity);
//                Cart cartSaved = cartRepository.save(cart);
//                return mapToCartResponse(cartSaved);
//            }
//        }
        if (quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than 0");
        }

        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> Cart.builder()
                .userId(userId)
                .quantity(0)
                .totalPrice(0.0)
                .cartItems(new ArrayList<>())
                .build());

        List<CartItem> cartItems = cart.getCartItems();
        ProductBaseResponse product = productService.getProductById(productId);

        ProductOptionResponse productOption;

        if (product == null) {
            throw new ResourceNotFoundException("Product", "id", productId + "");
        } else {
            productOption = product.getOptions()
                    .stream()
                    .filter(option -> option.getId().equals(productOptionId))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("ProductOption", "productOptionId", productOptionId + ""));
        }

        final boolean[] newCartItem = {false};
        double totalPrice = Double.parseDouble(productOption.getValue()) * quantity;

        CartItem cartItem = cartItems.stream()
                .filter(item -> item.getProductId().equals(productId) && item.getProductOptionId().equals(productOptionId))
                .findFirst()
                .orElseGet(() -> {
                    CartItem newItem = CartItem.builder()
                            .productId(productId)
                            .productName(product.getName())
                            .productOptionId(productOptionId)
                            .productSlug(product.getSlug())
                            .urlImageThumbnail(product.getImage())
                            .quantity(quantity)
                            .price(Double.parseDouble(productOption.getValue()))
                            .totalPrice(totalPrice)
                            .cart(cart)
                            .build();
                    cartItems.add(newItem);
                    newCartItem[0] = true;
                    return newItem;
                });

        if (!newCartItem[0]) {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setTotalPrice(cartItem.getTotalPrice() + totalPrice);
        }

        cart.setQuantity(cart.getQuantity() + quantity);
        cart.setTotalPrice(cart.getTotalPrice() + totalPrice);
        Cart cartSaved = cartRepository.save(cart);
        return mapToCartResponse(cartSaved);

    }

    @Override
    public CartResponse updateItemInCart(String userId, Long productId, Long itemId, Long productOptionId, Integer quantity) {
        //quantity update can be negative

        //get product
        ProductBaseResponse product = productService.getProductById(productId);

        //get cart items
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", userId));
        List<CartItem> cartItems = cart.getCartItems();

        //update quantity for cartItem
        CartItem cartItem = cartItems.stream()
                .filter(item -> item.getId().equals(itemId) && item.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "productId", productId + " check cart if not null and exist productId"));
        if (cartItem == null) {
            throw new ResourceNotFoundException("CartItem", "productId", productId + " check cart if not null and exist productId");
        } else {
            //this quantity calculated in client always greater than 0

            ProductOptionResponse por = product.getOptions()
                    .stream()
                    .filter(option -> option.getId().equals(productOptionId))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("ProductOption", "productOptionId", productOptionId + ""));

            if (!Objects.equals(cartItem.getProductOptionId(), productOptionId) &&
                    product.getOptions()
                            .stream()
                            .anyMatch(option -> option.getId().equals(productOptionId))) {
                cartItem.setProductOptionId(productOptionId);
            }
            try {
                cart.setQuantity(cart.getQuantity() - cartItem.getQuantity() + quantity);
                cart.setTotalPrice(cart.getTotalPrice() - cartItem.getTotalPrice() + Double.parseDouble(por.getValue()) * quantity);

                cartItem.setTotalPrice(Double.parseDouble(por.getValue()) * quantity);
                cartItem.setQuantity(quantity);
            } catch (Exception e) {
                log.error("error: {}", e.getMessage());
            }

            Cart cartSaved = cartRepository.save(cart);
            return mapToCartResponse(cartSaved);
        }
    }

    @Override
    public CartResponse removeItemFromCart(String userId, Long itemId) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", userId));
        List<CartItem> cartItems = cart.getCartItems();
        CartItem cartItem = cartItems.stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "itemId", itemId + ""));

        cart.setCartItems(cartItems);
        cart.setQuantity(cart.getQuantity() - cartItem.getQuantity());
        cart.setTotalPrice(cart.getTotalPrice() - cartItem.getTotalPrice());

        cartItems.remove(cartItem);
        cartItemRepository.delete(cartItem);

        Cart cartSaved = cartRepository.save(cart);
        return mapToCartResponse(cartSaved);
    }

    @Override
    public CartResponse clearCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", userId));
        cart.setCartItems(new ArrayList<>());
        cart.setQuantity(0);
        cart.setTotalPrice(0.0);
        cartItemRepository.deleteAll();
        Cart cartSaved = cartRepository.save(cart);
        return mapToCartResponse(cartSaved);
    }

    @Override
    public CartResponse checkoutCart(Long cartId) {
        return null;
    }

    @Override
    public Long getCartIdByUserId(String userId) {
        return cartRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", userId)).getId();
    }

    private Cart mapToCart(CartResponse cartByUserId) {
        return Cart.builder()
                .userId(cartByUserId.getUserId())

                .build();
    }

    private CartResponse mapToCartResponse(Cart cart) {
        return CartResponse.builder()
                .id(cart.getId())
                .cartItemResponses(mapToCartItemResponseList(cart.getCartItems()))
                .userId(cart.getUserId())
                .quantity(cart.getQuantity())
                .totalPrice(cart.getTotalPrice())
                .build();
    }

    private List<CartResponse> mapToCartResponseList(List<Cart> carts) {
        return carts.stream()
                .map(this::mapToCartResponse)
                .collect(Collectors.toList());
    }

    private CartItemResponse mapToCartItemResponse(CartItem cartItem) {
        List<ProductOptionResponse> productOptions = productService.getProductOptions(cartItem.getProductId());

        return CartItemResponse.builder()
                .id(cartItem.getId())
                .productId(cartItem.getProductId())
                .productName(cartItem.getProductName())
                .productOptionId(cartItem.getProductOptionId())
                .productOptions(productOptions)
                .urlImageThumbnail(cartItem.getUrlImageThumbnail())
                .productSlug(cartItem.getProductSlug())
                .productSlug(cartItem.getProductSlug())
                .quantity(cartItem.getQuantity())
                .price(cartItem.getPrice())
                .totalPrice(cartItem.getTotalPrice())
                .build();
    }

    private List<CartItemResponse> mapToCartItemResponseList(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(this::mapToCartItemResponse)
                .collect(Collectors.toList());
    }
}
