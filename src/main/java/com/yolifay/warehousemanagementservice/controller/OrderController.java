package com.yolifay.warehousemanagementservice.controller;

import com.yolifay.warehousemanagementservice.dto.ApiResponse;
import com.yolifay.warehousemanagementservice.dto.OrderCreateRequest;
import com.yolifay.warehousemanagementservice.dto.OrderResponse;
import com.yolifay.warehousemanagementservice.service.OrderService;
import com.yolifay.warehousemanagementservice.util.ResponseCode;
import com.yolifay.warehousemanagementservice.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Value("${service.id}")
    private String serviceId;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> createOrder(@Valid @RequestBody OrderCreateRequest req) {
        log.info("Incoming create order");

        OrderResponse data = orderService.create(req);
        ApiResponse body = ResponseUtil.build(ResponseCode.CREATED, serviceId, data);

        log.info("Outgoing create order {}", data.getId());
        return ResponseEntity.status(
                ResponseCode.CREATED.getHttpStatus())
                .body(body);
    }
}

