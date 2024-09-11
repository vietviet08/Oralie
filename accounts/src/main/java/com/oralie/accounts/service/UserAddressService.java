package com.oralie.accounts.service;

import com.oralie.accounts.dto.UserAddressDto;
import com.oralie.accounts.model.UserAddress;

import java.util.List;

public interface UserAddressService {
    UserAddressDto save(UserAddressDto userAddressDto);

    UserAddressDto update(UserAddressDto userAddress, Long id);

    void deleteById(Long id);
    void deleteByUserId(String userId);
    void deleteByUsername(String username);

    List<UserAddressDto> findAllByUserId(String userId, int page, int size, String sortBy, String sort);
    List<UserAddressDto> findAll(int page, int size, String sortBy, String sort);

}
