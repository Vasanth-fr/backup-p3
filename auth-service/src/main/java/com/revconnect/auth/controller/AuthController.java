package com.revconnect.auth.controller;

import com.revconnect.auth.dto.*;
import com.revconnect.auth.service.AuthService;
import com.revconnect.auth.service.JwtService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registration attempt for email: {}", request.getEmail());
        try {
            AuthResponse response = authService.register(request);
            log.info("User registered successfully: userId={}, email={}", response.getUserId(), response.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            log.warn("Registration failed for email={}: {}", request.getEmail(), e.getMessage());
            throw new RuntimeException("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for identifier: {}", request.getIdentifier());
        try {
            AuthResponse response = authService.login(request);
            log.info("Login successful: userId={}, email={}", response.getUserId(), response.getEmail());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.warn("Login failed for identifier={}: {}", request.getIdentifier(), e.getMessage());
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        log.debug("Token refresh requested");
        try {
            TokenRefreshResponse response = authService.refreshToken(request);
            log.debug("Token refreshed successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.warn("Token refresh failed: {}", e.getMessage());
            throw new RuntimeException("Token refresh failed: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Long userId = jwtService.extractUserId(token);
            log.info("Logout request for userId={}", userId);
            authService.logout(userId);
            log.info("Logout successful for userId={}", userId);
            return ResponseEntity.ok("Logged out successfully");
        } catch (Exception e) {
            log.warn("Logout failed: {}", e.getMessage());
            throw new RuntimeException("Logout failed: " + e.getMessage());
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            boolean isValid = jwtService.validateToken(token);
            log.debug("Token validation result: {}", isValid);
            return ResponseEntity.ok(isValid);
        } catch (Exception e) {
            log.warn("Token validation error: {}", e.getMessage());
            return ResponseEntity.ok(false);
        }
    }
}
