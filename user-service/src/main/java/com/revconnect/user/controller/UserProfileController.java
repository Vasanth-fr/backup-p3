package com.revconnect.user.controller;

import com.revconnect.user.dto.UserProfileRequest;
import com.revconnect.user.dto.UserProfileResponse;
import com.revconnect.user.dto.UserSummaryResponse;
import com.revconnect.user.dto.SyncUserRequest;
import com.revconnect.user.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile(
            @RequestHeader("X-User-Email") String userEmail) {
        UserProfileResponse profile = userProfileService.getProfile(userEmail);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @RequestHeader("X-User-Email") String userEmail,
            @Valid @RequestBody UserProfileRequest request) {
        UserProfileResponse profile = userProfileService.updateProfile(userEmail, request);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserSummaryResponse> getUserById(@PathVariable Long userId) {
        UserSummaryResponse user = userProfileService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/sync")
    public ResponseEntity<UserSummaryResponse> syncUser(@Valid @RequestBody SyncUserRequest request) {
        UserSummaryResponse user = userProfileService.syncUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserSummaryResponse>> searchUsers(
            @RequestParam(required = false) String query) {
        List<UserSummaryResponse> users = userProfileService.searchUsers(query);
        return ResponseEntity.ok(users);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    private static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
