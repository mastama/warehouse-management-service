package com.yolifay.warehousemanagementservice.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class OrderCreateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -4956123392051025469L;
    @NotEmpty
    private List<OrderLineRequest> lines;
}

