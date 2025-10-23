package com.yolifay.warehousemanagementservice.exception;

import com.yolifay.warehousemanagementservice.dto.ApiResponse;
import com.yolifay.warehousemanagementservice.util.ResponseCode;
import com.yolifay.warehousemanagementservice.util.ResponseUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 4 digit supaya format 3+4+3 pas (contoh: 0000). */
    @Value("${service.id}")
    private String serviceId;

    // === 409: constraint/duplicate dari DB
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        // Coba ambil nilai (opsional) dari pesan untuk di-append
        ex.getMostSpecificCause();
        String value = extractValue(ex.getMostSpecificCause().getMessage());
        String desc = appendValue(ResponseCode.DATA_EXISTS.getDescription(), value);
        ApiResponse body = ResponseUtil.build(ResponseCode.DATA_EXISTS, serviceId, desc, null);
        return ResponseEntity.status(ResponseCode.DATA_EXISTS.getHttpStatus()).body(body);
    }

    // === 400 vs 409: IllegalArgumentException – kalau mengandung "exist/duplicate/unique/already" → 409, selain itu 400
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArg(IllegalArgumentException ex) {
        String msg = Optional.ofNullable(ex.getMessage()).orElse("");
        if (looksLikeConflict(msg)) {
            String value = extractValue(msg);
            String desc = appendValue(ResponseCode.DATA_EXISTS.getDescription(), value);
            ApiResponse body = ResponseUtil.build(ResponseCode.DATA_EXISTS, serviceId, desc, null);
            return ResponseEntity.status(ResponseCode.DATA_EXISTS.getHttpStatus()).body(body);
        }
        ApiResponse body = ResponseUtil.build(ResponseCode.WRONG_DATA_FMT, serviceId, msg, null);
        return ResponseEntity.status(ResponseCode.WRONG_DATA_FMT.getHttpStatus()).body(body);
    }

    // === 400: @Valid body – pakai pesan field pertama saja, data=null
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleMethodArgNotValid(MethodArgumentNotValidException ex) {
        String firstMsg = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + " " + Optional.ofNullable(fe.getDefaultMessage()).orElse("Invalid"))
                .findFirst().orElse("Validation failed");
        ApiResponse body = ResponseUtil.build(ResponseCode.WRONG_DATA_FMT, serviceId, firstMsg, null);
        return ResponseEntity.status(ResponseCode.WRONG_DATA_FMT.getHttpStatus()).body(body);
    }

    // 400: out of stock / invalid state dari service
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse> handleIllegalState(IllegalStateException ex) {
        // respons 400 + desc = pesan dari service, data: null
        ApiResponse body = ResponseUtil.build(ResponseCode.OUT_OF_STOCK, serviceId, ex.getMessage(), null);
        return ResponseEntity.status(ResponseCode.OUT_OF_STOCK.getHttpStatus()).body(body);
    }

    // === 404
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse> handleNotFound(EntityNotFoundException ex) {
        ApiResponse body = ResponseUtil.build(ResponseCode.DATA_NOT_FOUND, serviceId, ex.getMessage(), null);
        return ResponseEntity.status(ResponseCode.DATA_NOT_FOUND.getHttpStatus()).body(body);
    }

    // === 405
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        ApiResponse body = ResponseUtil.build(ResponseCode.WRONG_DATA_FMT, serviceId, "HTTP method not supported", null);
        return ResponseEntity.status(ResponseCode.WRONG_DATA_FMT.getHttpStatus()).body(body);
    }

    // === 500 fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleOthers(Exception ex) {
        log.error("Unhandled exception", ex);
        ApiResponse body = ResponseUtil.build(ResponseCode.GENERAL_ERROR, serviceId, "Internal server error", null);
        return ResponseEntity.status(ResponseCode.GENERAL_ERROR.getHttpStatus()).body(body);
    }

    /* ---------------- helpers ---------------- */

    private static boolean looksLikeConflict(String msg) {
        String s = msg == null ? "" : msg.toLowerCase();
        return s.contains("exist") || s.contains("already") || s.contains("duplicate") || s.contains("unique");
    }

    /** Ambil value yang mau ditampilkan. Ubah seperlunya sesuai pola pesan exception di project-mu. */
    private static String extractValue(String message) {
        if (message == null || message.isBlank()) return null;
        int idx = message.indexOf(':');
        if (idx >= 0 && idx + 1 < message.length()) {
            return message.substring(idx + 1).trim();
        }
        if (message.contains("=")) {
            String[] parts = message.split("=", 2);
            return parts.length == 2 ? parts[1].trim() : message.trim();
        }
        return message.trim();
    }

    private static String appendValue(String baseDesc, String value) {
        return (value == null || value.isBlank()) ? baseDesc : baseDesc + " : " + value;
    }
}