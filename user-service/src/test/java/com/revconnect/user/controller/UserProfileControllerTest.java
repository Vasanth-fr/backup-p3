package com.revconnect.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revconnect.user.dto.*;
import com.revconnect.user.service.UserProfileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("UserProfileController Web Layer Tests")
class UserProfileControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean UserProfileService userProfileService;

    private UserProfileResponse sampleProfileResponse() {
        UserProfileResponse r = new UserProfileResponse();
        r.setId(1L); r.setEmail("alice@example.com");
        r.setFirstName("Alice"); r.setLastName("Smith");
        return r;
    }

    private UserSummaryResponse sampleSummary() {
        return UserSummaryResponse.builder()
                .id(1L).email("alice@example.com")
                .username("alice").fullName("Alice Smith").build();
    }

    @Test
    @DisplayName("GET /api/users/profile - returns 200 with profile")
    void getCurrentUserProfile_returns200() throws Exception {
        when(userProfileService.getProfile("alice@example.com"))
                .thenReturn(sampleProfileResponse());

        mockMvc.perform(get("/api/users/profile")
                        .header("X-User-Email", "alice@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.firstName").value("Alice"));
    }

    @Test
    @DisplayName("PUT /api/users/profile - updates and returns 200")
    void updateProfile_returns200() throws Exception {
        UserProfileRequest req = new UserProfileRequest();
        req.setFirstName("Alice"); req.setLastName("Smith");
        req.setBio("Engineer"); req.setLocation("NYC");

        when(userProfileService.updateProfile(eq("alice@example.com"), any(UserProfileRequest.class)))
                .thenReturn(sampleProfileResponse());

        mockMvc.perform(put("/api/users/profile")
                        .header("X-User-Email", "alice@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    @DisplayName("GET /api/users/{userId} - returns 200 with user summary")
    void getUserById_returns200() throws Exception {
        when(userProfileService.getUserById(1L)).thenReturn(sampleSummary());

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.fullName").value("Alice Smith"));
    }

    @Test
    @DisplayName("POST /api/users/sync - returns 201 with synced user")
    void syncUser_returns201() throws Exception {
        SyncUserRequest req = new SyncUserRequest();
        req.setId(1L); req.setEmail("alice@example.com");
        req.setFirstName("Alice"); req.setLastName("Smith");

        when(userProfileService.syncUser(any(SyncUserRequest.class))).thenReturn(sampleSummary());

        mockMvc.perform(post("/api/users/sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /api/users - returns 200 with list of users")
    void getAllUsers_returns200() throws Exception {
        when(userProfileService.getAllUsers()).thenReturn(List.of(sampleSummary()));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].email").value("alice@example.com"));
    }
}
