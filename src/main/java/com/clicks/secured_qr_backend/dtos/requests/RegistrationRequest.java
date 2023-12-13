package com.clicks.secured_qr_backend.dtos.requests;

public record RegistrationRequest(
        String email,
        String name,
        String password
) {
}
