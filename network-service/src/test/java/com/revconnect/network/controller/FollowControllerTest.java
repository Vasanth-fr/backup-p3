package com.revconnect.network.controller;

import com.revconnect.network.entity.Follow;
import com.revconnect.network.service.FollowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FollowController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("FollowController Web Layer Tests")
class FollowControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean FollowService followService;

    private Follow sampleFollow;

    @BeforeEach
    void setUp() {
        sampleFollow = new Follow(1L, 2L);
        try {
            java.lang.reflect.Field id = Follow.class.getDeclaredField("id");
            id.setAccessible(true); id.set(sampleFollow, 10L);
            java.lang.reflect.Field ts = Follow.class.getDeclaredField("createdAt");
            ts.setAccessible(true); ts.set(sampleFollow, LocalDateTime.now());
        } catch (Exception ignored) {}
    }

    @Test
    @DisplayName("POST /api/network/follow/{userId} - returns 201 on success")
    void follow_returns201() throws Exception {
        when(followService.follow(1L, 2L)).thenReturn(sampleFollow);

        mockMvc.perform(post("/api/network/follow/2")
                        .header("X-User-Id", "1")
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.followerId").value(1))
                .andExpect(jsonPath("$.followingId").value(2));
    }

    @Test
    @DisplayName("POST /api/network/follow/{userId} - already following returns 409")
    void follow_alreadyFollowing_returns409() throws Exception {
        when(followService.follow(1L, 2L))
                .thenThrow(new IllegalStateException("Already following this user"));

        mockMvc.perform(post("/api/network/follow/2")
                        .header("X-User-Id", "1")
                        .with(csrf()))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST /api/network/follow/{userId} - self-follow returns 400")
    void follow_selfFollow_returns400() throws Exception {
        when(followService.follow(1L, 1L))
                .thenThrow(new IllegalArgumentException("Cannot follow yourself"));

        mockMvc.perform(post("/api/network/follow/1")
                        .header("X-User-Id", "1")
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/network/follow/{userId} - returns 204 on success")
    void unfollow_returns204() throws Exception {
        mockMvc.perform(delete("/api/network/follow/2")
                        .header("X-User-Id", "1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(followService).unfollow(1L, 2L);
    }

    @Test
    @DisplayName("GET /api/network/is-following/{userId} - returns true when following")
    void isFollowing_returnsTrue() throws Exception {
        when(followService.isFollowing(1L, 2L)).thenReturn(true);

        mockMvc.perform(get("/api/network/is-following/2")
                        .header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("GET /api/network/follower-count/{userId} - returns count")
    void getFollowerCount_returnsCount() throws Exception {
        when(followService.getFollowerCount(2L)).thenReturn(42L);

        mockMvc.perform(get("/api/network/follower-count/2"))
                .andExpect(status().isOk())
                .andExpect(content().string("42"));
    }

    @Test
    @DisplayName("GET /api/network/following-count/{userId} - returns count")
    void getFollowingCount_returnsCount() throws Exception {
        when(followService.getFollowingCount(1L)).thenReturn(17L);

        mockMvc.perform(get("/api/network/following-count/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("17"));
    }

    @Test
    @DisplayName("GET /api/network/followers/{userId} - returns list of followers")
    void getFollowers_returnsList() throws Exception {
        when(followService.getFollowers(2L)).thenReturn(List.of(sampleFollow));

        mockMvc.perform(get("/api/network/followers/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].followerId").value(1));
    }

    @Test
    @DisplayName("GET /api/network/following/{userId} - returns list of following")
    void getFollowing_returnsList() throws Exception {
        when(followService.getFollowing(1L)).thenReturn(List.of(sampleFollow));

        mockMvc.perform(get("/api/network/following/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].followingId").value(2));
    }
}
