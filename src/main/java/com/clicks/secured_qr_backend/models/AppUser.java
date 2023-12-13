package com.clicks.secured_qr_backend.models;

import com.clicks.secured_qr_backend.enums.UserRole;
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
public class AppUser {

    @Id
    @GeneratedValue
    private Long id;
    private String email;
    private String name;
    private String password;
    private boolean verified;

    @Enumerated(EnumType.STRING)
    private UserRole role;

}
