package com.clicks.secured_qr_backend.dtos;

import java.util.List;

public record AppUserDto(
        String name,
        String email,
        String status,
        String role
) {
}
