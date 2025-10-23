package com.yolifay.warehousemanagementservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
public class ItemUpdateRequest implements Serializable {
    @NotBlank
    private String name;
    private String description;
}

