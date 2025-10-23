package com.yolifay.warehousemanagementservice.controller;

import com.yolifay.warehousemanagementservice.dto.ApiResponse;
import com.yolifay.warehousemanagementservice.dto.VariantCreateRequest;
import com.yolifay.warehousemanagementservice.dto.VariantResponse;
import com.yolifay.warehousemanagementservice.dto.VariantUpdateRequest;
import com.yolifay.warehousemanagementservice.service.VariantService;
import com.yolifay.warehousemanagementservice.util.ResponseCode;
import com.yolifay.warehousemanagementservice.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class VariantController {

    private final VariantService variantService;

    @Value("${service.id}")
    private String serviceId;

    @PostMapping(value = "/api/items/{itemId}/variants", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> createVariant(@PathVariable UUID itemId,
                                                     @Valid @RequestBody VariantCreateRequest req) {
        log.info("Incoming create variant for item {}", itemId);
        VariantResponse data = variantService.createVariant(itemId, req);
        ApiResponse body = ResponseUtil.build(ResponseCode.CREATED, serviceId, data);
        log.info("Outgoing create variant {}", data.getId());
        return ResponseEntity.status(ResponseCode.CREATED.getHttpStatus()).body(body);
    }

    @GetMapping("/api/items/{itemId}/variants")
    public ResponseEntity<ApiResponse> listVariantsByItem(@PathVariable UUID itemId) {
        log.info("Incoming list variants for item {}", itemId);
        List<VariantResponse> data = variantService.listVariantsByItem(itemId);
        ApiResponse body = ResponseUtil.build(ResponseCode.APPROVED, serviceId, data);
        log.info("Outgoing list variants for item {}", itemId);
        return ResponseEntity.status(ResponseCode.APPROVED.getHttpStatus()).body(body);
    }

    @GetMapping("/api/variants/{variantId}")
    public ResponseEntity<ApiResponse> getVariant(@PathVariable UUID variantId) {
        log.info("Incoming get variant {}", variantId);
        VariantResponse data = variantService.getVariant(variantId);
        ApiResponse body = ResponseUtil.build(ResponseCode.APPROVED, serviceId, data);
        log.info("Outgoing get variant {}", variantId);
        return ResponseEntity.status(ResponseCode.APPROVED.getHttpStatus()).body(body);
    }

    @PutMapping(value = "/api/variants/{variantId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> updateVariant(@PathVariable UUID variantId,
                                                     @Valid @RequestBody VariantUpdateRequest req) {
        log.info("Incoming update variant {}", variantId);
        VariantResponse data = variantService.updateVariant(variantId, req);
        ApiResponse body = ResponseUtil.build(ResponseCode.APPROVED, serviceId, data);
        log.info("Outgoing update variant {}", variantId);
        return ResponseEntity.status(ResponseCode.APPROVED.getHttpStatus()).body(body);
    }

    @DeleteMapping("/api/variants/{variantId}")
    public ResponseEntity<ApiResponse> deleteVariant(@PathVariable UUID variantId) {
        log.info("Incoming delete variant {}", variantId);
        variantService.deleteVariant(variantId);

        ApiResponse body = ResponseUtil.setResponse(
                ResponseCode.NO_CONTENT.getHttpStatus().value(),
                serviceId,
                ResponseCode.NO_CONTENT,
                null
        );

        log.info("Outgoing delete variant {}", variantId);
        return ResponseEntity.ok(body);
    }
}

