package com.yolifay.warehousemanagementservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class OrderLineRequest implements Serializable {
    @NotNull
    private UUID variantId;

    @NotNull @Min(1)
    private Integer quantity;
}

