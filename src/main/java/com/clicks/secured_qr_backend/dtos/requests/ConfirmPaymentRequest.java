package com.clicks.secured_qr_backend.dtos.requests;

public record ConfirmPaymentRequest(
    String reference,    
    String customerName,
    String customerEmail,
    String customerPhone

) {
} 
