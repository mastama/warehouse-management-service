package com.yolifay.warehousemanagementservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class StockSetRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -303752769302950638L;
    @NotNull @Min(0)
    private Integer stock; // set absolute
}

