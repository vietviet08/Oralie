package com.oralie.accounts.service.impl;

import com.oralie.accounts.dto.UserAddressDto;
import com.oralie.accounts.dto.entity.request.AddressRequest;
import com.oralie.accounts.dto.entity.response.AddressResponse;
import com.oralie.accounts.exception.ResourceNotFoundException;
import com.oralie.accounts.model.Account;
import com.oralie.accounts.model.UserAddress;
import com.oralie.accounts.repository.AccountsRepository;
import com.oralie.accounts.repository.UserAddressRepository;
import com.oralie.accounts.service.UserAddressService;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.orm.hibernate5.SpringSessionContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAddressServiceImpl implements UserAddressService {

    private final UserAddressRepository userAddressRepository;
    private final AccountsRepository accountsRepository;

    @Override
    public AddressResponse findById(Long idUserAddress) {
        return userAddressRepository.findById(idUserAddress)
                .map(this::mapToAddressResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User Address not found", "id", idUserAddress + ""));
    }

    @Override
    public AddressResponse save(AddressRequest addressRequest) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountsRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found", "userId", userId));

        if (account.getAddress().isEmpty()) {
            List<UserAddress> listUserAddress = new ArrayList<>();
            account.setAddress(listUserAddress);
        }

        account.getAddress().add(mapToUserAddress(addressRequest));

        accountsRepository.save(account);
//        UserAddress userAddress = userAddressRepository.save(mapToUserAddress(userAddressDto));
        return mapToAddressResponse(mapToUserAddress(addressRequest));
    }

    @Override
    public AddressResponse update(AddressRequest userAddress, Long idUserAddress) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountsRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found", "userId", userId));

        UserAddress address = account.getAddress().stream().filter(userAddress1 -> userAddress1.getId().equals(idUserAddress)).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("User Address not found", "id", idUserAddress + ""));

//        UserAddress userAddressFind = userAddressRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("User Address not found", "id", id+""));

        address.setAddressDetail(userAddress.getAddressDetail());
        address.setCity(userAddress.getCity());
        address.setPhone(userAddress.getPhone());

        accountsRepository.save(account);

        return mapToAddressResponse(address);
    }

    @Override
    public void deleteById(Long idUserAddress) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountsRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found", "userId", userId));

        boolean addressExists = account.getAddress().stream().anyMatch(address -> address.getId().equals(idUserAddress));
        if (!addressExists) {
            throw new ResourceNotFoundException("User Address not found", "id", idUserAddress + "");
        }

        userAddressRepository.deleteById(idUserAddress);
    }

    @Override
    public void deleteByUserId(String userId) {
        userAddressRepository.deleteByUserId(userId);
    }

    @Override
    public void deleteByUsername(String username) {
    }

    @Override
    public List<AddressResponse> findAllByUserId(String userId, int page, int size, String sortBy, String sort) {
        Sort sortObj = sort.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<UserAddress> userAddressDtoPage = userAddressRepository.findAllByUserId(userId, pageable);
        return mapToUserAddressResponseList(userAddressDtoPage.getContent());
    }

    @Override
    public List<AddressResponse> findAll(int page, int size, String sortBy, String sort) {
        Sort sortObj = sort.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<UserAddress> userAddressDtoPage = userAddressRepository.findAll(pageable);
        return mapToUserAddressResponseList(userAddressDtoPage.getContent());
    }

    private String getUsername(String userId) {
        Account account = accountsRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found", "userId", userId));
        return account.getUsername();
    }

    private List<UserAddressDto> mapToUserAddressDtoList(List<UserAddress> userAddressList) {

        return userAddressList.stream().map(userAddress ->
                UserAddressDto.builder()
                        .userId(userAddress.getUserId())
                        .phone(userAddress.getPhone())
                        .addressDetail(userAddress.getAddressDetail())
                        .city(userAddress.getCity())
                        .build()).toList();
    }

    private UserAddressDto mapToUserAddressDto(UserAddress userAddress) {
        return UserAddressDto.builder()
                .userId(userAddress.getUserId())
                .phone(userAddress.getPhone())
                .addressDetail(userAddress.getAddressDetail())
                .city(userAddress.getCity())
                .build();
    }

    private UserAddress mapToUserAddress(AddressRequest addressRequest) {
        return UserAddress.builder()
                .phone(addressRequest.getPhone())
                .addressDetail(addressRequest.getAddressDetail())
                .city(addressRequest.getCity())
                .build();
    }

    private AddressResponse mapToAddressResponse(UserAddress userAddress) {
        return AddressResponse.builder()
                .userId(userAddress.getUserId())
                .phone(userAddress.getPhone())
                .addressDetail(userAddress.getAddressDetail())
                .city(userAddress.getCity())
                .build();
    }

    private List<AddressResponse> mapToUserAddressResponseList(List<UserAddress> userAddressList) {
        return userAddressList.stream().map(userAddress ->
                AddressResponse.builder()
                        .userId(userAddress.getUserId())
                        .phone(userAddress.getPhone())
                        .addressDetail(userAddress.getAddressDetail())
                        .city(userAddress.getCity())
                        .build()).toList();
    }
}