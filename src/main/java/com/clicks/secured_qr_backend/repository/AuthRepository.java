package com.clicks.secured_qr_backend.repository;

import com.clicks.secured_qr_backend.models.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<AuthToken, Long> {
    Optional<AuthToken> findBySubject(String subject);

    Optional<AuthToken> findByOrigin (String remoteAddress);
}
