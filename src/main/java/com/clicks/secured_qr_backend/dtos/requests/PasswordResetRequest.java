package com.clicks.secured_qr_backend.dtos.requests;

public record PasswordResetRequest(
        String email,
        String password
) {
}
