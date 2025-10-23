package com.yolifay.warehousemanagementservice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class VariantUpdateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -2408449636785382247L;
    @NotBlank private String sku;
    private String size;
    private String color;

    @NotNull @DecimalMin("0.00")
    private BigDecimal price;

    @NotNull @Min(0)
    private Integer stock;
}

