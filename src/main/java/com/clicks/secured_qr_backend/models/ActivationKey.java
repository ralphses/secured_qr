package com.clicks.secured_qr_backend.models;

import com.clicks.secured_qr_backend.enums.TokenPurpose;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class ActivationKey {

    @Id
    @GeneratedValue
    private Long id;

    private String token;
    private String email;
    private Instant expiresAt;
    private Instant createdAt;
    private boolean used;
    private TokenPurpose purpose;

}
