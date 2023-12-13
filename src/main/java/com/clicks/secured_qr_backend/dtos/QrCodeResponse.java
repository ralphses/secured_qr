package com.clicks.secured_qr_backend.dtos;

public record QrCodeResponse(
        String link,
        String reference,
        String checkout,
        String createdAt,
        String itemName,
        double amount,
        String client
) {
}
