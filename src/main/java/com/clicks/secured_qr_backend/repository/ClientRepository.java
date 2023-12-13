package com.clicks.secured_qr_backend.repository;

import com.clicks.secured_qr_backend.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByReference(String reference);
    List<Client> findByUserId(Long userId);
}
