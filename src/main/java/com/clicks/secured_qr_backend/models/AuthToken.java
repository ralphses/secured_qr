package com.clicks.secured_qr_backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthToken {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 700, nullable = false)
    private String token;

    @DateTimeFormat
    private Instant expiresAt;

    private String origin;

    private String subject;
    private Boolean isLoggedIn;
    private Boolean isValid;
    @DateTimeFormat
    private Instant generatedAt;


}
