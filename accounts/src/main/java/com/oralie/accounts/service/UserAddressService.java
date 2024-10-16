package com.oralie.accounts.service;

import com.oralie.accounts.dto.UserAddressDto;
import com.oralie.accounts.dto.entity.request.AddressRequest;
import com.oralie.accounts.dto.entity.response.AddressResponse;
import com.oralie.accounts.model.UserAddress;

import java.util.List;

public interface UserAddressService {
    AddressResponse save(AddressRequest addressRequest);

    AddressResponse update(AddressRequest addressRequest, Long idUserAddress);

    void deleteById(Long idUserAddress);
    void deleteByUserId(String userId);
    void deleteByUsername(String username);

    List<AddressResponse> findAllByUserId(String userId, int page, int size, String sortBy, String sort);
    List<AddressResponse> findAll(int page, int size, String sortBy, String sort);

}
