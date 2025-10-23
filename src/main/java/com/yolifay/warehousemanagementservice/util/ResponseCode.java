package com.yolifay.warehousemanagementservice.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ResponseCode {
    APPROVED      ("000", "Approved",             HttpStatus.OK),
    CREATED       ("001", "Created",              HttpStatus.CREATED),
    NO_CONTENT    ("002", "No Content",           HttpStatus.NO_CONTENT),

    DATA_NOT_FOUND("014", "Data tidak ditemukan", HttpStatus.NOT_FOUND),
    DATA_EXISTS   ("017", "Data sudah ada",       HttpStatus.CONFLICT),
    WRONG_DATA_FMT("030", "Format Data Salah",    HttpStatus.BAD_REQUEST),
    GENERAL_ERROR ("098", "General Error",        HttpStatus.INTERNAL_SERVER_ERROR),
    OUT_OF_STOCK  ("031", "Stok tidak cukup",     HttpStatus.BAD_REQUEST);

    private final String code;
    private final String description;
    private final HttpStatus httpStatus;
}
