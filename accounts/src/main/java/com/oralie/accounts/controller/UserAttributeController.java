package com.oralie.accounts.controller;

import com.oralie.accounts.dto.entity.request.UserAttributeRequest;
import com.oralie.accounts.dto.entity.response.UserAttributeResponse;
import com.oralie.accounts.service.UserAttributeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(
        name = "CRUD REST APIs for User Attribute",
        description = "CREATE, READ, UPDATE, DELETE User Attribute"
)
@RequestMapping(produces = {"application/json"})
@RequiredArgsConstructor
public class UserAttributeController {

    private final UserAttributeService userAttributeService;

    //dash for manage
    @GetMapping("/dash/user-attribute")
    public ResponseEntity<List<UserAttributeResponse>> getAllUserAttribute(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sort
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userAttributeService.findAll(page, size, sortBy, sort));
    }

    @GetMapping("/dash/user-attribute/{userId}")
    public ResponseEntity<UserAttributeResponse> getUserAttributeByUserId(
            @PathVariable("userId") String userId
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userAttributeService.findAllByUserId(userId));
    }

    @GetMapping("/dash/user-attribute/{id}")
    public ResponseEntity<UserAttributeResponse> getUserAttributeByIdForManage(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userAttributeService.findById(id));
    }

    @DeleteMapping("/dash/user-attribute/{id}")
    public ResponseEntity<Void> deleteUserAttribute(@PathVariable Long id) {

        userAttributeService.deleteById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    //dash for self
    @GetMapping("/dash/profile/attribute")
    public ResponseEntity<UserAttributeResponse> getAllAttributeByUserIdForSelf() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userAttributeService.findAllByUserId(userId));
    }

    @GetMapping("/dash/profile/attribute/{id}")
    public ResponseEntity<UserAttributeResponse> getUserAttributeByIdForSelf(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userAttributeService.findById(id));
    }

    @PostMapping("/dash/profile/attribute")
    public ResponseEntity<UserAttributeResponse> saveUserAttributeForSelf(@RequestBody UserAttributeRequest userAttributeRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userAttributeService.save(userAttributeRequest));
    }

    @PutMapping("/dash/profile/attribute/{userId}")
    public ResponseEntity<UserAttributeResponse> updateUserAttributeForSelf(@RequestBody UserAttributeRequest userAttributeRequest,
                                                                          @PathVariable String userId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userAttributeService.update(userAttributeRequest, userId));
    }

    @DeleteMapping("/dash/profile/attribute/{id}")
    public ResponseEntity<Void> deleteUserAttributeByIdForSelf(@PathVariable Long id) {
        userAttributeService.deleteById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    //store
    @GetMapping("/store/user-attribute")
    public ResponseEntity<UserAttributeResponse> getAllAttributeByUserId() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userAttributeService.findAllByUserId(userId));
    }

    @GetMapping("/store/user-attribute/{id}")
    public ResponseEntity<UserAttributeResponse> getUserAttributeById(@PathVariable Long id) {
        
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userAttributeService.findById(id));
    }

    @PostMapping("/store/user-attribute")
    public ResponseEntity<UserAttributeResponse> saveUserAttribute(@RequestBody UserAttributeRequest userAttributeRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userAttributeService.save(userAttributeRequest));
    }

    @PutMapping("/store/user-attribute/{userId}")
    public ResponseEntity<UserAttributeResponse> updateUserAttribute(@RequestBody UserAttributeRequest userAttributeRequest,
                                                                   @PathVariable String userId) {
        String userIdContext = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!userId.equals(userIdContext)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userAttributeService.update(userAttributeRequest, userId));
    }

    @DeleteMapping("/store/user-attribute/{id}")
    public ResponseEntity<Void> deleteUserAttributeById(@PathVariable Long id) {
        userAttributeService.deleteById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }


}
