package com.yolifay.warehousemanagementservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "variants",
        uniqueConstraints = @UniqueConstraint(name="uk_item_sku", columnNames={"item_id","sku"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Variant implements Serializable {
    @Serial
    private static final long serialVersionUID = 443054632837996807L;
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name="item_id", nullable=false)
    private Item item;

    @Column(nullable=false, length=64)
    private String sku; // unik per item

    @Column(length=64)
    private String size;

    @Column(length=64)
    private String color;

    @Column(nullable=false, precision = 18, scale = 2)
    private BigDecimal price;

    @Column(nullable=false)
    private Integer stock; // non-negative

    @Version
    private Long version; // untuk optimistic locking
}

