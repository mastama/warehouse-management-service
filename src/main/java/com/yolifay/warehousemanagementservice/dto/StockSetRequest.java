package com.yolifay.warehousemanagementservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class StockSetRequest implements Serializable {
    @NotNull @Min(0)
    private Integer stock; // set absolute
}

