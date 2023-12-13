package com.clicks.secured_qr_backend.repository;

import com.clicks.secured_qr_backend.models.ActivationKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActivationKeyRepository extends JpaRepository<ActivationKey, Long> {

    Optional<ActivationKey> findByEmail(String email);
    Optional<ActivationKey> findByToken(String token);
}
