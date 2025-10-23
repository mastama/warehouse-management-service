package com.yolifay.warehousemanagementservice.dto;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class OrderResponse implements Serializable {
    private UUID id;
    private Instant createdAt;
    private BigDecimal grandTotal;
    private List<Line> lines;

    @Data @AllArgsConstructor @NoArgsConstructor @Builder
    public static class Line implements Serializable {
        private UUID variantId;
        private String sku;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal lineTotal;
    }
}

