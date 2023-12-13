package com.clicks.secured_qr_backend.service;

import com.clicks.secured_qr_backend.dtos.AppUserDto;
import com.clicks.secured_qr_backend.dtos.requests.RegistrationRequest;
import com.clicks.secured_qr_backend.enums.TokenPurpose;
import com.clicks.secured_qr_backend.enums.UserRole;
import com.clicks.secured_qr_backend.exceptions.ResourceExistsException;
import com.clicks.secured_qr_backend.exceptions.ResourceNotFoundException;
import com.clicks.secured_qr_backend.models.AppUser;
import com.clicks.secured_qr_backend.repository.AppUserRepository;
import com.clicks.secured_qr_backend.utils.DTOMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service class for managing application users.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ActivationKeyService activationKeyService;
    private final NotificationService notificationService;
    private final DTOMapper mapper;

    /**
     * Registers a new user with the provided registration request.
     *
     * @param registrationRequest The registration request containing user details.
     * @throws ResourceExistsException If a user with the given email already exists.
     */
    public void registerUser(RegistrationRequest registrationRequest) {
        String email = registrationRequest.email();

        if (userExists(email)) {
            throw new ResourceExistsException("User with email " + email + " already exists");
        }

        AppUser appUser = AppUser.builder()
                .role(UserRole.CLIENT)
                .name(registrationRequest.name())
                .email(email)
                .password(passwordEncoder.encode(registrationRequest.password()))
                .verified(true)
                .build();

        userRepository.save(appUser);

        String activationKey = activationKeyService.add(email, TokenPurpose.REGISTRATION);

        notificationService.sendActivationEmail(appUser.getName(), email, activationKey);
    }

    /**
     * Activates a user with the given email.
     *
     * @param email The email of the user to be activated.
     */
    public void activateUser(String email) {
        AppUser user = findByEmail(email);
        user.setVerified(true);
    }

    /**
     * Finds a user by email.
     *
     * @param email The email of the user to find.
     * @return The found user.
     * @throws ResourceNotFoundException If no user is found with the given email.
     */
    public AppUser findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email not found"));
    }

    /**
     * Checks if a user with the given email already exists.
     *
     * @param email The email to check.
     * @return true if a user with the given email exists, false otherwise.
     */
    public boolean userExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Gets an AppUserDto based on the username (email).
     *
     * @param username The username (email) of the user.
     * @return The AppUserDto representing the user.
     */
    public AppUserDto get(String username) {
        return mapper.appUserDto(findByEmail(username));
    }

}
