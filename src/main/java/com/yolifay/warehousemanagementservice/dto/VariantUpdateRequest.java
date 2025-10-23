package com.yolifay.warehousemanagementservice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class VariantUpdateRequest implements Serializable {
    @NotBlank private String sku;
    private String size;
    private String color;

    @NotNull @DecimalMin("0.00")
    private BigDecimal price;

    @NotNull @Min(0)
    private Integer stock;
}

