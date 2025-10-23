package com.yolifay.warehousemanagementservice.dto;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class VariantResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -3347989111208510155L;
    private UUID id;
    private UUID itemId;
    private String sku;
    private String size;
    private String color;
    private BigDecimal price;
    private Integer stock;
    private Long version;
}

