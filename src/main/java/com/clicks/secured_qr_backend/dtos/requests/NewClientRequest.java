package com.clicks.secured_qr_backend.dtos.requests;

import org.springframework.web.multipart.MultipartFile;

public record NewClientRequest(
        String name,
        String address,
        Integer numOfStaff,
        String yearCreated,
        String businessManagerPhone,
        String businessManagerName,
        MultipartFile nin,
        MultipartFile businessCertificate
) {
}
