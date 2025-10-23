package com.yolifay.warehousemanagementservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
/**
 * NOTE: Field 'data' bertipe Object; Sonar S1948 akan protes karena kelas ini Serializable.
 * Kita suppress karena JSON serialization, bukan Java serialization, yang dipakai.
 */
@SuppressWarnings("java:S1948")
public class ApiResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 4939973168847344264L;

    private String responseCode;
    private String responseDesc;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private Object data;
}