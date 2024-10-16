package com.oralie.accounts.controller;

import com.oralie.accounts.dto.UserAddressDto;
import com.oralie.accounts.dto.entity.request.AddressRequest;
import com.oralie.accounts.dto.entity.response.AddressResponse;
import com.oralie.accounts.service.UserAddressService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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

    //dash for manage
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

    @GetMapping("/dash/user-address/{id}")
    public ResponseEntity<AddressResponse> getUserAddressByIdForManage(@PathVariable Long id){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userAddressService.findById(id));
    }

    @DeleteMapping("/dash/user-address/{id}")
    public ResponseEntity<Void> deleteUserAddress(@PathVariable Long id){

        userAddressService.deleteById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    //dash for self
    @GetMapping("/dash/profile/address")
    public ResponseEntity<List<AddressResponse>> getAllAddressByUserIdForSelf(){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userAddressService.findAllByUserId(userId,0, 10, "id", "asc"));
    }

    @GetMapping("/dash/profile/address/{id}")
    public ResponseEntity<AddressResponse> getUserAddressByIdForSelf(@PathVariable Long id){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userAddressService.findById(id));
    }

    @PostMapping("/dash/profile/address")
    public ResponseEntity<AddressResponse> saveUserAddressForSelf(@RequestBody AddressRequest addressRequest){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userAddressService.save(addressRequest));
    }

    @PutMapping("/dash/profile/address/{id}")
    public ResponseEntity<AddressResponse> updateUserAddressForSelf(@RequestBody AddressRequest addressRequest,
                                                            @PathVariable Long id){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userAddressService.update(addressRequest, id));
    }

    @DeleteMapping("/dash/profile/address/{id}")
    public ResponseEntity<Void> deleteUserAddressByIdForSelf(@PathVariable Long id){
        userAddressService.deleteById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    //store
    @GetMapping("/store/user-address")
    public ResponseEntity<List<AddressResponse>> getAllAddressByUserId(){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userAddressService.findAllByUserId(userId,0, 10, "id", "asc"));
    }

    @GetMapping("/store/user-address/{id}")
    public ResponseEntity<AddressResponse> getUserAddressById(@PathVariable Long id){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userAddressService.findById(id));
    }

    @PostMapping("/store/user-address")
    public ResponseEntity<AddressResponse> saveUserAddress(@RequestBody AddressRequest addressRequest){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userAddressService.save(addressRequest));
    }

    @PutMapping("/store/user-address/{id}")
    public ResponseEntity<AddressResponse> updateUserAddress(@RequestBody AddressRequest addressRequest,
                                                            @PathVariable Long id){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userAddressService.update(addressRequest, id));
    }

    @DeleteMapping("/store/user-address/{id}")
    public ResponseEntity<Void> deleteUserAddressById(@PathVariable Long id){
        userAddressService.deleteById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }


}
