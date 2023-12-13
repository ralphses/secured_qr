package com.clicks.secured_qr_backend.controller;

import com.clicks.secured_qr_backend.dtos.CustomResponse;
import com.clicks.secured_qr_backend.dtos.requests.LoginRequest;
import com.clicks.secured_qr_backend.dtos.requests.PasswordResetRequest;
import com.clicks.secured_qr_backend.dtos.requests.RegistrationRequest;
import com.clicks.secured_qr_backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static java.util.Collections.emptyMap;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<CustomResponse> register(@RequestBody RegistrationRequest registrationRequest) {
        authService.register(registrationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CustomResponse(true, "Registration successful, check your email to activate your account"));
    }

    @PostMapping("/login")
    public ResponseEntity<CustomResponse> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        String token = authService.login(loginRequest, request);
        return ResponseEntity.ok(new CustomResponse(true, Map.of("token", token)));
    }

    @PostMapping("/logout")
    public ResponseEntity<CustomResponse> logout(Authentication authentication, HttpServletRequest request) {
        authService.logout(authentication, request);
        return ResponseEntity.ok(new CustomResponse(true, emptyMap()));
    }

    @PostMapping("/password-reset")
    public ResponseEntity<CustomResponse> passwordReset(@RequestBody PasswordResetRequest passwordResetRequest) {
        authService.passwordResetRequest(passwordResetRequest);
        return ResponseEntity.ok(new CustomResponse(true, ""));
    }

    @GetMapping("/password-reset")
    public ResponseEntity<CustomResponse> passwordReset(@RequestParam(name = "email") String email) {
        String returnedEmail = authService.passwordResetRequest(email);
        return ResponseEntity.ok(new CustomResponse(true, Map.of("email", returnedEmail)));
    }

    @GetMapping("/activate")
    public ResponseEntity<CustomResponse> activate(@RequestParam("token") String token) {
        authService.activate(token);
        return ResponseEntity.ok(new CustomResponse(true, ""));
    }

}
