package com.oralie.orders.dto.response;

import com.oralie.orders.model.OrderAddress;
import jakarta.persistence.SecondaryTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private String userId;
    private Long cartId;
    private OrderAddressResponse address;
    private List<OrderItemResponse> orderItems;
    private Double totalPrice;
    private String voucher;
    private Double discount;
    private Double shippingFee;
    private String status;
    private String shippingMethod;
    private String paymentMethod;
    private String paymentStatus;
    private String note;
}
