package com.yolifay.warehousemanagementservice.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OrderCreateRequest implements Serializable {
    @NotEmpty
    private List<OrderLineRequest> lines;
}

