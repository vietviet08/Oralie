package com.oralie.accounts.controller;

import com.oralie.accounts.dto.UserAddressDto;
import com.oralie.accounts.service.UserAddressService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(
        name = "CRUD REST APIs for User Address",
        description = "CREATE, READ, UPDATE, DELETE User Address"
)
@RequestMapping(produces = {"application/json"})
@RequiredArgsConstructor
public class UserAddressController {

    private final UserAddressService userAddressService;

    @GetMapping("/dash/user-address")
    public ResponseEntity<List<UserAddressDto>> getAllUserAddress(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sort
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userAddressService.findAll(page, size, sortBy, sort));
    }

    @GetMapping("/dash/user-address/{userId}")
    public ResponseEntity<List<UserAddressDto>> getUserAddressByUserId(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sort,
            @RequestParam() String userId
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userAddressService.findAllByUserId(userId, page, size, sortBy, sort));
    }

    @PostMapping("/store/user-address/save")
    public ResponseEntity<UserAddressDto> saveUserAddress(@RequestBody UserAddressDto userAddressDto){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userAddressService.save(userAddressDto));
    }

    @PutMapping("/store/user-address/update/{id}")
    public ResponseEntity<UserAddressDto> updateUserAddress(@RequestBody UserAddressDto userAddressDto,
                                                            @PathVariable Long id){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userAddressService.update(userAddressDto, id));
    }

    @DeleteMapping("/dash/user-address/delete/{id}")
    public ResponseEntity<Void> deleteUserAddress(@PathVariable Long id){

        userAddressService.deleteById(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
