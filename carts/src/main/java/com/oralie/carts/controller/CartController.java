package com.oralie.carts.controller;

import com.oralie.carts.dto.CartContactDto;
import com.oralie.carts.dto.response.CartResponse;
import com.oralie.carts.dto.response.ListResponse;
import com.oralie.carts.service.CartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Tag(
        name = "CRUD REST APIs for Carts",
        description = "The API of Cart Service"
)
@RestController
@RequiredArgsConstructor
@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
public class CartController {

    private final Environment environment;

    @Value("${info.app.version}")
    private String build;

    private final CartContactDto cartContactDto;

    private final CartService cartService;

    @GetMapping("/dash/carts")
    public ResponseEntity<ListResponse<CartResponse>> getAllCart(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sort
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(cartService.getAllCarts(page, size, sortBy, sort));
    }

    @DeleteMapping("/dash/carts/{id}")
    public HttpStatus deleteCart(@PathVariable("id") Long id){
        cartService.deleteCart(id);
        return HttpStatus.OK;
    }

    @GetMapping("/store/carts")
    //

    @PostMapping(value = "/store/carts/add-to-cart")
    public ResponseEntity<CartResponse> addProductToCart(){
        return null;
    }

    @PutMapping("/store/carts/remove")
    public ResponseEntity<CartResponse> removeItemInCart(){
        return null;
    }

    @PutMapping("/store/carts/clear")
    public ResponseEntity<CartResponse> clearCart(){
        return null;
    }

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
