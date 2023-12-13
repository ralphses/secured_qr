package com.clicks.secured_qr_backend.dtos.requests;

public record LoginRequest(
        String email,
        String password
) {
}
