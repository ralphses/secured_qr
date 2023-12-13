package com.clicks.secured_qr_backend.service;

import com.clicks.secured_qr_backend.dtos.requests.LoginRequest;
import com.clicks.secured_qr_backend.dtos.requests.PasswordResetRequest;
import com.clicks.secured_qr_backend.dtos.requests.RegistrationRequest;
import com.clicks.secured_qr_backend.exceptions.ResourceNotFoundException;
import com.clicks.secured_qr_backend.exceptions.UnauthorizedUserException;
import com.clicks.secured_qr_backend.models.ActivationKey;
import com.clicks.secured_qr_backend.models.AppUser;
import com.clicks.secured_qr_backend.models.AuthToken;
import com.clicks.secured_qr_backend.repository.AuthRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.HOURS;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final PasswordEncoder passwordEncoder;
    private final AuthRepository authRepository;
    private final AppUserService userService;
    private final ActivationKeyService activationKeyService;

    public String login(LoginRequest loginRequest, HttpServletRequest request) {

        // Authenticate the user based on the provided username and password.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()));

        // Get the username from the authenticated user.
        String username = authentication.getName();
        // Get the login origin (client IP address) from the request.
        String loginOrigin = request.getRemoteAddr();

        // Check if there is an existing authentication token for the user.
        Optional<AuthToken> userOptional = authRepository.findBySubject(username);

        userOptional.ifPresent(authRepository::delete);

        // Generate timestamps for token expiration.
        Instant now = Instant.now();
        Instant expiresAt = now.plus(12, HOURS);

        // Generate the scope for the access token based on user authorities.
        String scope = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        // Build the JWT claims set with issuer, issuedAt, expiresAt, subject, and
        // custom claims.
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .issuer("ccm")
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(username)
                .claim("claim", scope)
                .claim("origin", loginOrigin)
                .build();

        // Encode the JWT with HS512 algorithm using the claims set.
        var encoderParams = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS512).build(), jwtClaimsSet);
        String accessToken = jwtEncoder.encode(encoderParams).getTokenValue();

        // Create a new authentication token and save it.
        AuthToken token = AuthToken.builder()
                .origin(loginOrigin)
                .generatedAt(now)
                .expiresAt(expiresAt)
                .isValid(true)
                .token(accessToken)
                .subject(username)
                .isLoggedIn(true)
                .build();

        authRepository.save(token);

        // Return the generated access token.
        return accessToken;
    }

    public void register(RegistrationRequest registrationRequest) {
        userService.registerUser(registrationRequest);
    }

    public void passwordResetRequest(PasswordResetRequest passwordResetRequest) {
        AppUser user = userService.findByEmail(passwordResetRequest.email());
        user.setPassword(passwordEncoder.encode(passwordResetRequest.password()));
    }

    public String passwordResetRequest(String email) {
        if (userService.userExists(email)) {
            return email;
        } else
            throw new ResourceNotFoundException("email not found");
    }

    public void activate(String token) {
        ActivationKey activate = activationKeyService.activate(token);
        userService.activateUser(activate.getEmail());

    }

    public void logout(Authentication authentication, HttpServletRequest request) {

        Optional<String> tokenOptional = Optional.ofNullable(((JwtAuthenticationToken) authentication)
                .getToken()
                .getTokenValue());

        String user = authentication.getName();
        String requestRemoteAddress = request.getRemoteAddr();

        if (tokenOptional.isEmpty()) {
            log.debug("Empty token passed by user {} at {}", user, Instant.now());
            throw new UnauthorizedUserException("Invalid or empty token");
        }

        String requestTokenString = tokenOptional.get();

        try {

            Jwt decodedJwtString = jwtDecoder.decode(requestTokenString);

            AuthToken authToken = findTokenByRemoteAddress(requestRemoteAddress);

            if (!isValidToken(authToken, requestRemoteAddress, decodedJwtString)) {

                log.info("Invalid token passed {}", requestTokenString);
                throw new UnauthorizedUserException("Invalid or empty token");
            }

            authToken.setIsLoggedIn(false);
            authToken.setIsValid(false);

            log.info("User {} logged out from address {} ", user, requestRemoteAddress);

        } catch (JwtException e) {

            log.debug("Empty or invalid token passed by user {} at {} from {}",
                    user, Instant.now(), requestRemoteAddress);

            throw new UnauthorizedUserException("Invalid or empty token");
        }

    }

    private AuthToken findTokenByRemoteAddress(String remoteAddress) {
        return authRepository.findByOrigin(remoteAddress)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or Illegal Token"));
    }

    private boolean isValidToken(AuthToken authToken, String requestRemoteAddress, Jwt decodedString) {

        String tokenFromDb = authToken.getToken();

        return authToken.getIsLoggedIn() &&
                Objects.equals(requestRemoteAddress, decodedString.getClaimAsString("origin")) &&
                Objects.equals(authToken.getSubject(), decodedString.getSubject()) &&
                Objects.equals(tokenFromDb, decodedString.getTokenValue()) &&
                authToken.getExpiresAt().isAfter(Instant.now());
    }
}
