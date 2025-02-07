package com.oralie.accounts.service.impl;

import com.oralie.accounts.dto.entity.request.UserAttributeRequest;
import com.oralie.accounts.dto.entity.response.UserAttributeResponse;
import com.oralie.accounts.exception.ResourceNotFoundException;
import com.oralie.accounts.model.Account;
import com.oralie.accounts.model.UserAttribute;
import com.oralie.accounts.repository.AccountsRepository;
import com.oralie.accounts.repository.UserAttributeRepository;
import com.oralie.accounts.service.UserAttributeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAttributeServiceImpl implements UserAttributeService {

    private final UserAttributeRepository userAttributeRepository;
    private final AccountsRepository accountsRepository;

    @Override
    public UserAttributeResponse findById(Long idUserAddress) {
        return userAttributeRepository.findById(idUserAddress)
                .map(this::mapToAddressResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User Address not found", "id", idUserAddress + ""));
    }

    @Override
    public UserAttributeResponse save(UserAttributeRequest userAttributeRequest) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountsRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found", "userId", userId));

        UserAttribute userAddress = mapToUserAddress(userAttributeRequest);
        userAddress.setAccount(account);
        userAddress = userAttributeRepository.save(userAddress);

        return mapToAddressResponse(userAddress);
    }

    @Override
    public UserAttributeResponse update(UserAttributeRequest userAttributeRequest, String userId) {
        String userIdContext = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountsRepository.findByUserId(userIdContext)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found", "userId", userIdContext));

        UserAttribute userAddress = userAttributeRepository.findAllByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User Address not found", "userId", userId));

        userAddress.setPhone(userAttributeRequest.getPhone());
        userAddress.setAddressDetail(userAttributeRequest.getAddress());
        userAddress.setCity(userAttributeRequest.getCity());
        userAddress.setAccount(account);
        userAddress = userAttributeRepository.save(userAddress);

        return mapToAddressResponse(userAddress);
    }

    @Transactional
    @Override
    public void deleteById(Long idUserAttribute) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountsRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found", "userId", userId));

        UserAttribute userAddress = userAttributeRepository.findById(idUserAttribute)
                .orElseThrow(() -> new ResourceNotFoundException("User Address not found", "id", idUserAttribute + ""));

        if (!userAddress.getAccount().getUserId().equals(account.getUserId())) {
            throw new ResourceNotFoundException("User Address not found", "id", idUserAttribute + "");
        } else {
            userAttributeRepository.deleteById(idUserAttribute);
        }
    }

    @Transactional
    @Override
    public void deleteByUserId(String userId) {
        userAttributeRepository.deleteByUserId(userId);
    }

    @Transactional
    @Override
    public void deleteByUsername(String username) {
    }

    @Override
    public UserAttributeResponse findAllByUserId(String userId) {
        UserAttribute userAddressDtoPage = userAttributeRepository.findAllByUserId(userId).orElseThrow();
        return mapToAddressResponse(userAddressDtoPage);
    }

    @Override
    public List<UserAttributeResponse> findAll(int page, int size, String sortBy, String sort) {
        Sort sortObj = sort.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<UserAttribute> userAddressDtoPage = userAttributeRepository.findAll(pageable);
        return mapToUserAddressResponseList(userAddressDtoPage.getContent());
    }

    private UserAttribute mapToUserAddress(UserAttributeRequest userAttributeRequest) {
        return UserAttribute.builder()
                .phone(userAttributeRequest.getPhone())
                .addressDetail(userAttributeRequest.getAddress())
                .city(userAttributeRequest.getCity())
                .build();
    }

    private UserAttributeResponse mapToAddressResponse(UserAttribute userAttribute) {
        return UserAttributeResponse.builder()
                .userId(userAttribute.getAccount().getUserId())
                .phone(userAttribute.getPhone())
                .address(userAttribute.getAddressDetail())
                .city(userAttribute.getCity())
                .build();
    }

    private List<UserAttributeResponse> mapToUserAddressResponseList(List<UserAttribute> userAttributeList) {
        return userAttributeList.stream().map(userAttribute ->
                UserAttributeResponse.builder()
                        .userId(userAttribute.getAccount().getUserId())
                        .phone(userAttribute.getPhone())
                        .address(userAttribute.getAddressDetail())
                        .city(userAttribute.getCity())
                        .build()).toList();
    }
}