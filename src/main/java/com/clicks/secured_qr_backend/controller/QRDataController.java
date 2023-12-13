package com.clicks.secured_qr_backend.controller;

import com.clicks.secured_qr_backend.dtos.CustomResponse;
import com.clicks.secured_qr_backend.dtos.QrCodeResponse;
import com.clicks.secured_qr_backend.dtos.requests.QrCodeDto;
import com.clicks.secured_qr_backend.dtos.requests.VerifyQrCodeRequest;
import com.clicks.secured_qr_backend.service.QRCodeDataService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
public class QRDataController {

    private final QRCodeDataService qrCodeDataService;

    @PostMapping("/api/v1/qr")
    public ResponseEntity<CustomResponse> create(@RequestBody QrCodeDto qrCodeDto, HttpServletRequest request) {
        QrCodeResponse qrCodeResponse = qrCodeDataService.create(qrCodeDto, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CustomResponse(true, Map.of("qr", qrCodeResponse)));
    }

    @GetMapping("/api/v1/qr")
    public ResponseEntity<CustomResponse> get(HttpServletRequest request) {
        List<QrCodeResponse> qrCodeResponses = qrCodeDataService.getAll(request);
        return ResponseEntity.status(OK).body(new CustomResponse(true, Map.of("codes", qrCodeResponses)));
    }

    @GetMapping("/qr/{reference}")
    public ResponseEntity<Resource> get(@PathVariable String reference) {
        Resource qrCode = qrCodeDataService.get(reference);
        return ResponseEntity.ok()
                .header(CONTENT_TYPE, IMAGE_PNG_VALUE)
                .header(CONTENT_DISPOSITION, "attachment; filename=" + reference + ".png")
                .body(qrCode);
    }

    @GetMapping("/qr")
    public ResponseEntity<CustomResponse> verify(@RequestBody VerifyQrCodeRequest verifyQrCodeRequest) {
        String url = qrCodeDataService.verify(verifyQrCodeRequest);
        return ResponseEntity.status(OK).body(new CustomResponse(true, Map.of("link", url)));

    }

    @PostMapping(value = "/image/qr")
    public ResponseEntity<CustomResponse> verifyImage(@RequestParam("qr") MultipartFile qrCode) {
        String url = qrCodeDataService.verifyImage(qrCode);
        return ResponseEntity.status(OK).body(new CustomResponse(true, Map.of("link", url)));
    }
}
