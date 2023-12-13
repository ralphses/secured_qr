package com.clicks.secured_qr_backend.dtos.requests;

public record NewPaymentRequest(
        double amount,
        String item,
        String description,
        String client
) {
}
