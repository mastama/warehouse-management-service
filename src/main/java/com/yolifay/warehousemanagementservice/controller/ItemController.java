package com.yolifay.warehousemanagementservice.controller;

import com.yolifay.warehousemanagementservice.dto.ApiResponse;
import com.yolifay.warehousemanagementservice.dto.ItemCreateRequest;
import com.yolifay.warehousemanagementservice.dto.ItemUpdateRequest;
import com.yolifay.warehousemanagementservice.service.ItemService;
import com.yolifay.warehousemanagementservice.util.ResponseCode;
import com.yolifay.warehousemanagementservice.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @Value("${service.id}")
    private String serviceId;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> createItem(@Valid @RequestBody ItemCreateRequest req) {
        log.info("Incoming create item");
        var data = itemService.createItem(req);
        var body = ResponseUtil.build(ResponseCode.CREATED, serviceId, data);

        log.info("Outgoing create item");
        return ResponseEntity.status(
                ResponseCode.CREATED.getHttpStatus())
                .body(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getItem(@PathVariable UUID id) {
        log.info("Incoming get item: {}", id);
        var data = itemService.getItem(id);
        var body = ResponseUtil.build(ResponseCode.APPROVED, serviceId, data);
        log.info("Outgoing get item: {}", id);
        return ResponseEntity.status(
                ResponseCode.APPROVED.getHttpStatus())
                .body(body);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> updateItem(@PathVariable UUID id,
                                                  @Valid @RequestBody ItemUpdateRequest req) {
        log.info("Incoming update item: {}", id);
        var data = itemService.updateItem(id, req);
        var body = ResponseUtil.build(ResponseCode.APPROVED, serviceId, data);
        log.info("Outgoing update item: {}", id);
        return ResponseEntity.status(
                ResponseCode.APPROVED.getHttpStatus())
                .body(body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteItem(@PathVariable UUID id) {
        log.info("Incoming delete item: {}", id);
        itemService.deleteItem(id);

        var body = ResponseUtil.setResponse(
                ResponseCode.NO_CONTENT.getHttpStatus().value(),
                serviceId,
                ResponseCode.NO_CONTENT,
                null
        );

        log.info("Outgoing delete item: {}", id);
        return ResponseEntity.ok(body);
    }


}
