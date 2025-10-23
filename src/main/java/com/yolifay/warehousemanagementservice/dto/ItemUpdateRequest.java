package com.yolifay.warehousemanagementservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ItemUpdateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1771852932427204163L;
    @NotBlank
    private String name;
    private String description;
}

