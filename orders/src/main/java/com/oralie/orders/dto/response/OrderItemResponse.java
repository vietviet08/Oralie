package com.oralie.orders.dto.response;

import com.oralie.orders.dto.entity.BaseEntity;
import com.oralie.orders.model.Order;
import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemResponse {

    private Long id;
    private Long productId;
    private String productName;
    private String productImage;
    private int quantity;
    private Double totalPrice;

}