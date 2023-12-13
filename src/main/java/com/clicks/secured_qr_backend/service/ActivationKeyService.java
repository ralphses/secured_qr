package com.clicks.secured_qr_backend.service;

import com.clicks.secured_qr_backend.enums.TokenPurpose;
import com.clicks.secured_qr_backend.exceptions.InvalidParamsException;
import com.clicks.secured_qr_backend.exceptions.ResourceNotFoundException;
import com.clicks.secured_qr_backend.models.ActivationKey;
import com.clicks.secured_qr_backend.repository.ActivationKeyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Service class for managing activation keys.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ActivationKeyService {

    private final ActivationKeyRepository activationKeyRepository;

    /**
     * Generates and adds a new activation key to the repository.
     *
     * @param email   The email associated with the activation key.
     * @param purpose The purpose of the activation key.
     * @return The generated activation token.
     */
    public String add(String email, TokenPurpose purpose) {
        Instant now = Instant.now();
        String token = UUID.randomUUID().toString();

        ActivationKey activationKey = ActivationKey.builder()
                .token(token)
                .createdAt(now)
                .expiresAt(now.plus(20, ChronoUnit.MINUTES))
                .purpose(purpose)
                .used(false)
                .email(email)
                .build();

        activationKeyRepository.save(activationKey);
        return token;
    }

    /**
     * Activates the user with the given activation token.
     *
     * @param token The activation token.
     * @return The activated ActivationKey entity.
     * @throws ResourceNotFoundException If the activation token is not found.
     * @throws InvalidParamsException    If the activation link is no longer valid.
     */
    public ActivationKey activate(String token) {
        ActivationKey activationKey = activationKeyRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid activation token"));

        if (activationKey.isUsed() || activationKey.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidParamsException("Activation link is no longer valid");
        }

        activationKey.setUsed(true);
        return activationKey;
    }
}
