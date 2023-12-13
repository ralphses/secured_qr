package com.clicks.secured_qr_backend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QRCodeData {

    @Id
    @GeneratedValue
    private Long id;

    private String reference;
    private String checkOutUrl;
    private String itemName;
    private Double amount;
    private LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(name = "client", foreignKey = @ForeignKey(name = "client_payment_fk"))
    private Client client;

}
