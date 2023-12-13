package com.clicks.secured_qr_backend.controller;

import com.clicks.secured_qr_backend.dtos.AppUserDto;
import com.clicks.secured_qr_backend.dtos.CustomResponse;
import com.clicks.secured_qr_backend.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class AppUserController {

    private final AppUserService userService;

    /**
     * Retrieves user information by username.
     *
     * @param username The username of the user to retrieve.
     * @return ResponseEntity containing a CustomResponse with user information.
     */
    @GetMapping("/{username}")
    public ResponseEntity<CustomResponse> get(@PathVariable String username) {

        // Call the service to get user information
        AppUserDto user = userService.get(username);

        // Create a CustomResponse and wrap the user information
        CustomResponse customResponse = new CustomResponse(true, Map.of("user", user));

        // Return the response entity with the CustomResponse
        return ResponseEntity.ok(customResponse);
    }
}
