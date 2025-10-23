package com.yolifay.warehousemanagementservice.dto;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class ItemResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -2619507359204026265L;
    private UUID id;
    private String name;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
}

