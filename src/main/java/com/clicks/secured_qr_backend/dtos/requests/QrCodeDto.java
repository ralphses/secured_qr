package com.clicks.secured_qr_backend.dtos.requests;

public record QrCodeDto(
        String checkOutUrl,
        String item,
        double amount

) {
}
