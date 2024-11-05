package com.oralie.orders.model;

import com.oralie.orders.dto.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "`order`")
@Builder
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private Long cartId;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "order_address_id", referencedColumnName = "order_address_id")
    private OrderAddress address;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

    private Double totalPrice;
    private String voucher;
    private Double discount;
    private Double shippingFee;
    private String status;
    private String shippingMethod;
    private String paymentMethod;
    private String paymentStatus;
    private String note;

    // PayPal(optional)
    private String linkPaypalToExecute;
    private String payId;
}
