package com.yolifay.warehousemanagementservice.util;

import com.yolifay.warehousemanagementservice.dto.ApiResponse;

public final class ResponseUtil {
    private ResponseUtil() { throw new IllegalStateException("Utility class"); }

    private static String pad3(int n)        { return String.format("%03d", n); }
    private static String pad4(String s) {
        if (s == null || s.isBlank()) return "0000";
        String trimmed = s.trim();
        // jaga 4 char – kalau kepanjangan, ambil 4 terakhir
        if (trimmed.length() > 4) trimmed = trimmed.substring(trimmed.length() - 4);
        return String.format("%4s", trimmed).replace(' ', '0');
    }

    /** Pola baku: code = HTTP(3) + serviceId(4) + rc.code(3). Boleh override description. */
    public static ApiResponse build(ResponseCode rc, String serviceId, String descOverride, Object data) {
        String code = pad3(rc.getHttpStatus().value()) + pad4(serviceId) + rc.getCode();
        String desc = (descOverride != null && !descOverride.isBlank()) ? descOverride : rc.getDescription();
        return ApiResponse.builder()
                .responseCode(code)
                .responseDesc(desc)
                .data(data)
                .build();
    }

    /** Versi ringkas tanpa override description. */
    public static ApiResponse build(ResponseCode rc, String serviceId, Object data) {
        return build(rc, serviceId, null, data);
    }

    /** Kompatibilitas: kalau kamu ingin pakai httpStatus eksplisit + rc (tanpa override desc). */
    public static ApiResponse setResponse(int httpStatus, String serviceId, ResponseCode rc, Object data) {
        String code = pad3(httpStatus) + pad4(serviceId) + rc.getCode();
        return ApiResponse.builder()
                .responseCode(code)
                .responseDesc(rc.getDescription())
                .data(data)
                .build();
    }
}

