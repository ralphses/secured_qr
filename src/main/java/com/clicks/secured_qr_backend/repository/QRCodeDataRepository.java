package com.clicks.secured_qr_backend.repository;

import com.clicks.secured_qr_backend.models.QRCodeData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QRCodeDataRepository extends JpaRepository<QRCodeData, Long> {

    Optional<QRCodeData> findByReference(String reference);
    List<QRCodeData> findByClient_Id(Long id);

    boolean existsByCheckOutUrl(String url);
}
