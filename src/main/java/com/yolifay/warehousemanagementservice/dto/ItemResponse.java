package com.yolifay.warehousemanagementservice.dto;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class ItemResponse implements Serializable {
    private UUID id;
    private String name;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
}

