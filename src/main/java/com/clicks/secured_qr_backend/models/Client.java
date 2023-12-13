package com.clicks.secured_qr_backend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Client {

    @Id
    @GeneratedValue
    private Long id;

    private String businessName;
    private String apiKey;
    private String address;
    private String reference;
    private String ninPath;
    private String businessCertPath;
    private String businessManager;
    private String businessManagerPhone;
    private String yearCreated;
    private Integer noOfStaff;

    @ManyToOne()
    @JoinColumn(name = "owner",foreignKey = @ForeignKey(name = "client_owner_fk"))
    private AppUser user;

}
