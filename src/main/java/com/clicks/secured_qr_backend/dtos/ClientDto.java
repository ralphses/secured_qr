package com.clicks.secured_qr_backend.dtos;

public record ClientDto(
        String name,
        String address,
        String apiKey,
        String reference,
        String manager,
        String managerPhone,
        String yearCreated,
        int noOfStaff
) {
}
