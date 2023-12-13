package com.clicks.secured_qr_backend.repository;

import com.clicks.secured_qr_backend.models.ClientPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientPaymentRepository extends JpaRepository<ClientPayment, Long> {
    Optional<ClientPayment> findByReference(String reference);
}
