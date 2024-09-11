package com.oralie.accounts.service.impl;

import com.oralie.accounts.dto.UserAddressDto;
import com.oralie.accounts.model.UserAddress;
import com.oralie.accounts.service.UserAddressService;

import java.util.List;

public class UserAddressServiceImpl implements UserAddressService {
    @Override
    public UserAddressDto save(UserAddressDto userAddressDto) {
        return null;
    }

    @Override
    public UserAddressDto findById(Long id) {
        return null;
    }

    @Override
    public UserAddressDto findByUserId(Long id) {
        return null;
    }

    @Override
    public UserAddressDto findByUsername(Long id) {
        return null;
    }

    @Override
    public List<UserAddressDto> findAll() {
        return List.of();
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public void deleteByUserId(String userId) {

    }

    @Override
    public void deleteByUsername(String username) {

    }

    @Override
    public UserAddressDto update(UserAddressDto userAddress) {
        return null;
    }
}
