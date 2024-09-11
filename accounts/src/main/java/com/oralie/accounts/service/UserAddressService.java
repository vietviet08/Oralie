package com.oralie.accounts.service;

import com.oralie.accounts.dto.UserAddressDto;
import com.oralie.accounts.model.UserAddress;

import java.util.List;

public interface UserAddressService {
    UserAddressDto save(UserAddressDto userAddressDto);
    UserAddressDto findById(Long id);
    UserAddressDto findByUserId(Long id);
    UserAddressDto findByUsername(Long id);
    List<UserAddressDto> findAll();
    void deleteById(Long id);
    void deleteByUserId(String userId);
    void deleteByUsername(String username);
    UserAddressDto update(UserAddressDto userAddress);
}
