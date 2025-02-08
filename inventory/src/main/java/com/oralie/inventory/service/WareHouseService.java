package com.oralie.inventory.service;

import com.oralie.inventory.dto.request.WareHouseRequest;
import com.oralie.inventory.dto.response.ListResponse;
import com.oralie.inventory.dto.response.WareHouseResponse;

import java.util.List;

public interface WareHouseService {

    ListResponse<WareHouseResponse> getAllWareHouses(int page, int size, String sortBy, String sort, String search);

    WareHouseResponse getWareHouseById(Long id);

    WareHouseResponse createWareHouse(WareHouseRequest wareHouseRequest);

    WareHouseResponse updateWareHouse(Long id, WareHouseRequest wareHouseRequest);

    void deleteWareHouse(Long id);
}
