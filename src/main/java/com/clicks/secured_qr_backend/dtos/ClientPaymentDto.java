package com.clicks.secured_qr_backend.dtos;

public record ClientPaymentDto(

        String reference,
        String itemName,
        String description,
        double amount,
        String createdAt
) {
}
