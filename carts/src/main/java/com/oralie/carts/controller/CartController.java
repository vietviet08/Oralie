package com.oralie.carts.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.oralie.carts.dto.CartContactDto;
import com.oralie.carts.dto.response.CartItemResponse;
import com.oralie.carts.dto.response.CartResponse;
import com.oralie.carts.dto.response.ListResponse;
import com.oralie.carts.service.CartService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "CRUD REST APIs for Carts", description = "The API of Cart Service")
@RestController
@RequiredArgsConstructor
@RequestMapping(produces = { MediaType.APPLICATION_JSON_VALUE })
public class CartController {

  private static final Logger log = LoggerFactory.getLogger(CartController.class);

  private final Environment environment;

  @Value("${info.app.version}")
  private String build;

  private final CartContactDto cartContactDto;

  private final CartService cartService;

  // dash
  @GetMapping("/dash/carts")
  public ResponseEntity<ListResponse<CartResponse>> getAllCart(
      @RequestParam(required = false, defaultValue = "0") int page,
      @RequestParam(required = false, defaultValue = "10") int size,
      @RequestParam(required = false, defaultValue = "id") String sortBy,
      @RequestParam(required = false, defaultValue = "asc") String sort) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(cartService.getAllCarts(page, size, sortBy, sort));
  }

  @GetMapping("/dash/carts/{id}")
  public ResponseEntity<List<CartItemResponse>> getCartById(@PathVariable("id") Long id) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(cartService.getCartItemByCartId(id));
  }

  @PostMapping("/dash/carts")
  public ResponseEntity<CartResponse> createCart() {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(cartService.createCart(SecurityContextHolder.getContext().getAuthentication().getName()));
  }

  @DeleteMapping("/dash/carts/{id}")
  public HttpStatus deleteCart(@PathVariable("id") Long id) {
    cartService.deleteCart(id);
    return HttpStatus.OK;
  }

  // store
  @GetMapping("/store/carts/get-cart-id")
  public ResponseEntity<Long> getCartIdByUserId() {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(cartService.getCartIdByUserId(SecurityContextHolder.getContext().getAuthentication().getName()));
  }

  @GetMapping("/store/carts")
  public ResponseEntity<CartResponse> getCart() {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(cartService.getCartByUserId(SecurityContextHolder.getContext().getAuthentication().getName()));
  }

  @GetMapping("/store/carts/items")
  public ResponseEntity<List<CartItemResponse>> getItemFromCart() {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(cartService.getCartItemByUserId(SecurityContextHolder.getContext().getAuthentication().getName()));
  }

  @PostMapping(value = "/store/carts/add/{productId}")
  public ResponseEntity<CartResponse> addProductToCart(
      @PathVariable("productId") Long productId,
      @RequestParam("optionId") Long optionId,
      @RequestParam("quantity") Integer quantity) {

    String userId = SecurityContextHolder.getContext().getAuthentication().getName();
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(cartService.addItemToCart(userId, productId, optionId, quantity));
  }

  @PutMapping("/store/carts/update/{productId}")
  public ResponseEntity<CartResponse> updateItemInCart(
      @PathVariable("productId") Long productId,
      @RequestParam("itemId") Long itemId,
      @RequestParam("optionId") Long optionId,
      @RequestParam("quantity") Integer quantity) {
    log.info("updateItemInCart request: {}", quantity);
    String userId = SecurityContextHolder.getContext().getAuthentication().getName();
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(cartService.updateItemInCart(userId, productId, itemId, optionId, quantity));
  }

  @DeleteMapping("/store/carts/remove/{productId}")
  public ResponseEntity<CartResponse> removeItemInCart(@PathVariable("productId") Long productId) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(cartService.removeItemFromCart(SecurityContextHolder.getContext().getAuthentication().getName(),
            productId));
  }

  @DeleteMapping("/store/carts/clear")
  public ResponseEntity<CartResponse> clearCart() {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(cartService.clearCart(SecurityContextHolder.getContext().getAuthentication().getName()));
  }

  // info services
  @GetMapping("/carts/build-version")
  public ResponseEntity<String> getBuildVersion() {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(build);
  }

  @GetMapping("/carts/java-version")
  public ResponseEntity<String> getJavaVersion() {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(environment.getProperty("JAVA_HOME"));
  }

  @GetMapping("/carts/contact-info")
  public ResponseEntity<CartContactDto> getProductsContactDto() {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(cartContactDto);
  }

}
