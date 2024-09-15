package com.oralie.products.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProductAlreadyExistException extends RuntimeException {
    public ProductAlreadyExistException(String message) {
        super(message);
    }
}
