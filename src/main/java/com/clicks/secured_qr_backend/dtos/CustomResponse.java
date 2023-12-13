package com.clicks.secured_qr_backend.dtos;

public record CustomResponse(
        boolean success,
        Object data
) {
}
