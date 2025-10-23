package com.yolifay.warehousemanagementservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name="sales_order_lines")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SalesOrderLine implements Serializable {
    @Serial
    private static final long serialVersionUID = 31683173206235429L;
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    @JoinColumn(name="order_id")
    private SalesOrder order;

    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    @JoinColumn(name="variant_id")
    private Variant variant;

    @Column(nullable=false)
    private Integer quantity;

    @Column(nullable=false, precision=18, scale=2)
    private BigDecimal unitPrice;

    @Column(nullable=false, precision=18, scale=2)
    private BigDecimal lineTotal;
}

