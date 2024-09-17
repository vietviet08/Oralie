package com.oralie.products.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "CRUD REST APIs for Products",
        description = "CREATE, READ, UPDATE, DELETE Products"
)
@RestController
@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
public class ProductController {



}
