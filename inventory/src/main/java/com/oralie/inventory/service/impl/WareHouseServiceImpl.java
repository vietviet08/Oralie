package com.oralie.inventory.service.impl;

import com.oralie.inventory.dto.request.WareHouseRequest;
import com.oralie.inventory.dto.response.ListResponse;
import com.oralie.inventory.dto.response.WareHouseResponse;
import com.oralie.inventory.exception.ResourceNotFoundException;
import com.oralie.inventory.model.Warehouse;
import com.oralie.inventory.repository.WareHouseRepository;
import com.oralie.inventory.service.WareHouseService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
    private final WareHouseRepository wareHouseRepository;


    @Override
    public List<WareHouseResponse> getAllWareHouses() {
        List<Warehouse> wareHouses = wareHouseRepository.findAll();
        return mapToListWareHouseResponse(wareHouses);
    }


    @Override
    public ListResponse<WareHouseResponse> getAllWareHouses(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Warehouse> pageWareHouse = wareHouseRepository.findAll(pageable);
        List<Warehouse> warehouses = pageWareHouse.getContent();

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
        Warehouse wareHouse = wareHouseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("WareHouse not found", "id", id.toString()));
        return mapToWareHouseResponse(wareHouse);
    }

    @Override
    public WareHouseResponse createWareHouse(WareHouseRequest wareHouseRequest) {
        Warehouse wareHouse = Warehouse.builder()
                .name(wareHouseRequest.getName())
                .address(wareHouseRequest.getAddress())
                .build();
        Warehouse savedWareHouse = wareHouseRepository.save(wareHouse);
        return mapToWareHouseResponse(savedWareHouse);
    }

    @Override
    public WareHouseResponse updateWareHouse(Long id, WareHouseRequest wareHouseRequest) {
        Warehouse wareHouse = wareHouseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("WareHouse not found", "id", id.toString()));
        wareHouse.setName(wareHouseRequest.getName());
        wareHouse.setAddress(wareHouseRequest.getAddress());
        Warehouse updatedWareHouse = wareHouseRepository.save(wareHouse);
        return mapToWareHouseResponse(updatedWareHouse);
    }

    @Override
    public void deleteWareHouse(Long id) {
        Warehouse wareHouse = wareHouseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("WareHouse not found", "id", id.toString()));
        wareHouseRepository.delete(wareHouse);
    }

    private WareHouseResponse mapToWareHouseResponse(Warehouse wareHouse) {
        return WareHouseResponse.builder()
                .id(wareHouse.getId())
                .name(wareHouse.getName())
                .address(wareHouse.getAddress())
                .build();
    }

    private List<WareHouseResponse> mapToListWareHouseResponse(List<Warehouse> wareHouses) {
        return wareHouses.stream()
                .map(wareHouse -> WareHouseResponse.builder()
                        .id(wareHouse.getId())
                        .name(wareHouse.getName())
                        .address(wareHouse.getAddress())
                        .build())
                .collect(Collectors.toList());
    }
}
