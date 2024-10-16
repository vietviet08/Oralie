package com.oralie.accounts.controller;

import com.oralie.accounts.dto.UserAddressDto;
import com.oralie.accounts.dto.entity.request.AddressRequest;
import com.oralie.accounts.dto.entity.response.AddressResponse;
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
    public ResponseEntity<List<AddressResponse>> getAllUserAddress(
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
    public ResponseEntity<List<AddressResponse>> getUserAddressByUserId(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sort,
            @PathVariable("userId") String userId
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userAddressService.findAllByUserId(userId, page, size, sortBy, sort));
    }

    @DeleteMapping("/dash/user-address/delete/{id}")
    public ResponseEntity<Void> deleteUserAddress(@PathVariable Long id){

        userAddressService.deleteById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @PostMapping("/store/user-address/save")
    public ResponseEntity<AddressResponse> saveUserAddress(@RequestBody AddressRequest addressRequest){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userAddressService.save(addressRequest));
    }

    @PutMapping("/store/user-address/update/{id}")
    public ResponseEntity<AddressResponse> updateUserAddress(@RequestBody AddressRequest addressRequest,
                                                            @PathVariable Long id){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userAddressService.update(addressRequest, id));
    }


}
