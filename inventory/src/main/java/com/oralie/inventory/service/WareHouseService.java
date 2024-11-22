package com.oralie.inventory.service;

import com.oralie.inventory.dto.request.WareHouseRequest;
import com.oralie.inventory.dto.response.ListResponse;
import com.oralie.inventory.dto.response.WareHouseResponse;

import java.util.List;

public interface WareHouseService {

    List<WareHouseResponse> getAllWareHouses();

    ListResponse<WareHouseResponse> getAllWareHouses(int pageNo, int pageSize);

    WareHouseResponse getWareHouseById(Long id);

    WareHouseResponse createWareHouse(WareHouseRequest wareHouseRequest);

    WareHouseResponse updateWareHouse(Long id, WareHouseRequest wareHouseRequest);

    void deleteWareHouse(Long id);
}
