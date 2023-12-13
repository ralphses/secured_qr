package com.clicks.secured_qr_backend.controller;

import com.clicks.secured_qr_backend.dtos.ClientPaymentDto;
import com.clicks.secured_qr_backend.dtos.CustomResponse;
import com.clicks.secured_qr_backend.dtos.QrCodeResponse;
import com.clicks.secured_qr_backend.dtos.requests.ConfirmPaymentRequest;
import com.clicks.secured_qr_backend.dtos.requests.NewPaymentRequest;
import com.clicks.secured_qr_backend.service.ClientPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("payment")
public class ClientPaymentController {

    private final ClientPaymentService clientPaymentService;

    @PostMapping
    public ResponseEntity<CustomResponse> create(@RequestBody NewPaymentRequest paymentRequest) {
        QrCodeResponse qrCodeResponse = clientPaymentService.create(paymentRequest);
        return ResponseEntity.status(CREATED).body(new CustomResponse(true, Map.of("qr", qrCodeResponse)));
    }

    @GetMapping
    public ResponseEntity<CustomResponse> get(Authentication authentication) {
        List<QrCodeResponse> qrCodeResponses = clientPaymentService.get(authentication);
        return ResponseEntity.status(OK).body(new CustomResponse(true, Map.of("codes", qrCodeResponses)));
    }

    @PostMapping("/confirm")
    public ResponseEntity<CustomResponse> confirm(@RequestBody ConfirmPaymentRequest confirmPaymentRequest) {
        System.out.println(confirmPaymentRequest);
        String link = clientPaymentService.confirm(confirmPaymentRequest);
        return ResponseEntity.status(CREATED).body(new CustomResponse(true, Map.of("link", link)));
    }

    @GetMapping("/get/{reference}")
    public ResponseEntity<CustomResponse> checkout(@PathVariable String reference) {
        ClientPaymentDto paymentDto = clientPaymentService.checkout(reference);
        System.out.println(paymentDto);
        return ResponseEntity.status(OK).body(new CustomResponse(true, Map.of("payment", paymentDto)));
    }


}
