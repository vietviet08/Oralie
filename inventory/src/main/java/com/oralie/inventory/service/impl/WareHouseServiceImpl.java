package com.oralie.inventory.service.impl;

import com.oralie.inventory.dto.request.WareHouseRequest;
import com.oralie.inventory.dto.response.ListResponse;
import com.oralie.inventory.dto.response.WareHouseResponse;
import com.oralie.inventory.exception.ResourceNotFoundException;
import com.oralie.inventory.model.WareHouse;
import com.oralie.inventory.repository.WareHouseRepository;
import com.oralie.inventory.service.WareHouseService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class WareHouseServiceImpl implements WareHouseService {

    private static final Logger log = LoggerFactory.getLogger(InventoryServiceImpl.class);

    private final WareHouseRepository wareHouseRepository;

    @Override
    public List<WareHouseResponse> getAllWareHouses() {
        List<WareHouse> wareHouses = wareHouseRepository.findAll();
        log.info("wareHouse size is {}, wareHouses.size().toString()");
        return mapToListWareHouseResponse(wareHouses);
    }


    @Override
    public ListResponse<WareHouseResponse> getAllWareHouses(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<WareHouse> pageWareHouse = wareHouseRepository.findAll(pageable);
        List<WareHouse> warehouses = pageWareHouse.getContent();

        return ListResponse
                .<WareHouseResponse>builder()
                .data(mapToListWareHouseResponse(warehouses))
                .pageNo(pageWareHouse.getNumber())
                .pageSize(pageWareHouse.getSize())
                .totalElements((int) pageWareHouse.getTotalElements())
                .totalPages(pageWareHouse.getTotalPages())
                .isLast(pageWareHouse.isLast())
                .build();
    }

    @Override
    public WareHouseResponse getWareHouseById(Long id) {
        WareHouse wareHouse = wareHouseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("WareHouse not found", "id", id.toString()));
        return mapToWareHouseResponse(wareHouse);
    }

    @Override
    public WareHouseResponse createWareHouse(WareHouseRequest wareHouseRequest) {
        WareHouse wareHouse = WareHouse.builder()
                .name(wareHouseRequest.getName())
                .address(wareHouseRequest.getAddress())
                .build();
        WareHouse savedWareHouse = wareHouseRepository.save(wareHouse);
        return mapToWareHouseResponse(savedWareHouse);
    }

    @Override
    public WareHouseResponse updateWareHouse(Long id, WareHouseRequest wareHouseRequest) {
        WareHouse wareHouse = wareHouseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("WareHouse not found", "id", id.toString()));
        wareHouse.setName(wareHouseRequest.getName());
        wareHouse.setAddress(wareHouseRequest.getAddress());
        WareHouse updatedWareHouse = wareHouseRepository.save(wareHouse);
        return mapToWareHouseResponse(updatedWareHouse);
    }

    @Override
    public void deleteWareHouse(Long id) {
        WareHouse wareHouse = wareHouseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("WareHouse not found", "id", id.toString()));
        wareHouseRepository.delete(wareHouse);
    }

    private WareHouseResponse mapToWareHouseResponse(WareHouse wareHouse) {
        return WareHouseResponse.builder()
                .id(wareHouse.getId())
                .name(wareHouse.getName())
                .address(wareHouse.getAddress())
                .build();
    }

    private List<WareHouseResponse> mapToListWareHouseResponse(List<WareHouse> wareHouses) {
        return wareHouses.stream()
                .map(wareHouse -> WareHouseResponse.builder()
                        .id(wareHouse.getId())
                        .name(wareHouse.getName())
                        .address(wareHouse.getAddress())
                        .build())
                .collect(Collectors.toList());
    }
}
