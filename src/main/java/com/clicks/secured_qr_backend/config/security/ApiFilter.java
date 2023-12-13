package com.clicks.secured_qr_backend.config.security;

import com.clicks.secured_qr_backend.exceptions.UnauthorizedUserException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.clicks.secured_qr_backend.utils.AppUtils.REF_SEP;

/**
 * Custom filter for handling API key-based authentication.
 */
@Component
@RequiredArgsConstructor
public class ApiFilter extends OncePerRequestFilter {

    private final ApiKeyUtils apiKeyUtils;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * Filters incoming requests to authenticate using API key.
     *
     * @param request     The HTTP servlet request.
     * @param response    The HTTP servlet response.
     * @param filterChain The filter chain to proceed with after authentication.
     * @throws ServletException If a servlet exception occurs.
     * @throws IOException      If an I/O exception occurs.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Retrieve the Authorization header from the request
        String authorization = request.getHeader("Authorization");

        // If the Authorization header is not present or doesn't start with "Bearer API-", proceed with the filter chain
        if (authorization == null || !authorization.startsWith("Bearer API-")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // If the Authorization header starts with "Bearer API-", extract and process the API key
            if (authorization.startsWith("Bearer API-")) {

                // Extract the API key and decrypt it
                String apiKey = authorization.replace("Bearer API-", "");
                String decryptedKey = apiKeyUtils.decrypt(apiKey);

                // Extract the username from the decrypted key and load user details
                String username = decryptedKey.split(REF_SEP)[0];
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                // Create an authentication token and set authentication details
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set the authentication token in the security context and proceed with the filter chain
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                filterChain.doFilter(request, response);
            } else {
                // Handle any other cases if needed
            }

        } catch (Exception exception) {
            // Throw an UnauthorizedUserException in case of invalid authorization header
            throw new UnauthorizedUserException("Invalid authorization header");
        }
    }
}
