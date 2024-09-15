package com.oralie.accounts.service.impl;

import com.oralie.accounts.dto.UserAddressDto;
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
    public UserAddressDto save(UserAddressDto userAddressDto) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountsRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found", "userId", userId));

        if (account.getAddress().isEmpty()) {
            List<UserAddress> listUserAddress = new ArrayList<>();
            account.setAddress(listUserAddress);
        }

        account.getAddress().add(mapToUserAddress(userAddressDto));

        accountsRepository.save(account);
//        UserAddress userAddress = userAddressRepository.save(mapToUserAddress(userAddressDto));
        return mapToUserAddressDto(mapToUserAddress(userAddressDto));
    }

    @Override
    public UserAddressDto update(UserAddressDto userAddress, Long idUserAddress) {
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

        return mapToUserAddressDto(address);
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
    public List<UserAddressDto> findAllByUserId(String userId, int page, int size, String sortBy, String sort) {
        Sort sortObj = sort.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<UserAddress> userAddressDtoPage = userAddressRepository.findAllByUserId(userId, pageable);
        return mapToUserAddressDtoList(userAddressDtoPage.getContent());
    }

    @Override
    public List<UserAddressDto> findAll(int page, int size, String sortBy, String sort) {
        Sort sortObj = sort.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<UserAddress> userAddressDtoPage = userAddressRepository.findAll(pageable);
        return mapToUserAddressDtoList(userAddressDtoPage.getContent());
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
                        .username(getUsername(userAddress.getUserId()))
                        .phone(userAddress.getPhone())
                        .addressDetail(userAddress.getAddressDetail())
                        .city(userAddress.getCity())
                        .build()).toList();
    }

    private UserAddressDto mapToUserAddressDto(UserAddress userAddress) {
        return UserAddressDto.builder()
                .userId(userAddress.getUserId())
                .username(getUsername(userAddress.getUserId()))
                .phone(userAddress.getPhone())
                .addressDetail(userAddress.getAddressDetail())
                .city(userAddress.getCity())
                .build();
    }

    private UserAddress mapToUserAddress(UserAddressDto userAddressDto) {
        return UserAddress.builder()
                .userId(userAddressDto.getUserId())
                .phone(userAddressDto.getPhone())
                .addressDetail(userAddressDto.getAddressDetail())
                .city(userAddressDto.getCity())
                .build();
    }
}