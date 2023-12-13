package com.clicks.secured_qr_backend.controller;

import com.clicks.secured_qr_backend.dtos.ClientDto;
import com.clicks.secured_qr_backend.dtos.CustomResponse;
import com.clicks.secured_qr_backend.dtos.requests.NewClientRequest;
import com.clicks.secured_qr_backend.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("client")
public class ClientController {

    private final ClientService clientService;

    @PostMapping()
    public ResponseEntity<CustomResponse> create(

            @RequestParam("name") String name,
            @RequestParam("address") String address,
            @RequestParam("numOfStaff") Integer numOfStaff,
            @RequestParam("yearCreated") String yearCreated,
            @RequestParam("businessManagerPhone") String businessManagerPhone,
            @RequestParam("businessManagerName") String businessManagerName,
            @RequestParam("nin") MultipartFile nin,
            @RequestParam("businessCertificate") MultipartFile businessCertificate,
            Authentication authentication) {

        NewClientRequest newClientRequest = new NewClientRequest(name,address, numOfStaff, yearCreated, businessManagerPhone, businessManagerName, nin, businessCertificate);
        String apiKey = clientService.create(newClientRequest, authentication);
        return ResponseEntity.status(CREATED).body(new CustomResponse(true, Map.of("api", apiKey)));
    }


    @GetMapping()
    public ResponseEntity<CustomResponse> get(Authentication authentication) {
        List<ClientDto> clients = clientService.get(authentication);
        return ResponseEntity.status(OK).body(new CustomResponse(true, Map.of("clients", clients)));
    }
}
