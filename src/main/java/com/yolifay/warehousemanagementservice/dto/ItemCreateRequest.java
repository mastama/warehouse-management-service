package com.yolifay.warehousemanagementservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ItemCreateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -431294877524143478L;
    @NotBlank
    private String name;
    private String description;
}
