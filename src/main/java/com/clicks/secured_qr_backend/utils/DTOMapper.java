package com.clicks.secured_qr_backend.utils;

import com.clicks.secured_qr_backend.dtos.AppUserDto;
import com.clicks.secured_qr_backend.dtos.ClientDto;
import com.clicks.secured_qr_backend.dtos.ClientPaymentDto;
import com.clicks.secured_qr_backend.dtos.QrCodeResponse;
import com.clicks.secured_qr_backend.models.AppUser;
import com.clicks.secured_qr_backend.models.Client;
import com.clicks.secured_qr_backend.models.ClientPayment;
import com.clicks.secured_qr_backend.models.QRCodeData;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

import static com.clicks.secured_qr_backend.utils.AppUtils.getUrl;
import static java.util.Collections.emptyMap;

@Component
public class DTOMapper {

    public ClientDto clientDto(Client client) {
        System.out.println("client = " + client);
        return new ClientDto(
                client.getBusinessName(),
                client.getAddress(),
                client.getApiKey(),
                client.getReference(),
                client.getBusinessManager(),
                client.getBusinessManagerPhone(),
                client.getYearCreated(),
                client.getNoOfStaff());
    }

    public ClientPaymentDto clientPaymentDto (ClientPayment clientPayment) {

        return new ClientPaymentDto(
                clientPayment.getReference(),
                clientPayment.getItemName(),
                clientPayment.getDescription(),
                clientPayment.getAmount(),
                clientPayment.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy h:ma"))
        );
    }

    public AppUserDto appUserDto (AppUser user) {
        return new AppUserDto(
                user.getName(),
                user.getEmail(),
                user.isVerified() ? "Verified" : "Not verified",
                user.getRole().name()
        );
    }

    public QrCodeResponse qrCodeResponse(QRCodeData qrCodeData) {
        return new QrCodeResponse(
                getUrl("/qr/" + qrCodeData.getReference(), emptyMap()),
                qrCodeData.getReference(),
                qrCodeData.getCheckOutUrl(),
                qrCodeData.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy h:ma")),
                qrCodeData.getItemName(),
                qrCodeData.getAmount(),
                qrCodeData.getClient().getBusinessName());
    }
}
