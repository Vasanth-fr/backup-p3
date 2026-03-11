package com.revconnect.interaction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revconnect.interaction.dto.LikeRequest;
import com.revconnect.interaction.dto.LikeResponse;
import com.revconnect.interaction.service.LikeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LikeController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("LikeController Web Layer Tests")
class LikeControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean LikeService likeService;

    private LikeResponse sampleResponse() {
        return LikeResponse.builder()
                .id(1L).postId(100L).userId(10L)
                .createdAt(LocalDateTime.now()).build();
    }

    @Test
    @DisplayName("POST /api/interactions/likes - returns 201 on success")
    void likePost_returns201() throws Exception {
        LikeRequest req = new LikeRequest();
        req.setPostId(100L);
        when(likeService.likePost(any(LikeRequest.class), eq(10L))).thenReturn(sampleResponse());

        mockMvc.perform(post("/api/interactions/likes")
                        .header("X-User-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.postId").value(100))
                .andExpect(jsonPath("$.userId").value(10));
    }

    @Test
    @DisplayName("POST /api/interactions/likes - duplicate like returns 409")
    void likePost_duplicateLike_returns409() throws Exception {
        LikeRequest req = new LikeRequest();
        req.setPostId(100L);
        when(likeService.likePost(any(), anyLong()))
                .thenThrow(new IllegalStateException("Post already liked by user"));

        mockMvc.perform(post("/api/interactions/likes")
                        .header("X-User-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("DELETE /api/interactions/likes/{postId} - returns 204 on success")
    void unlikePost_returns204() throws Exception {
        mockMvc.perform(delete("/api/interactions/likes/100")
                        .header("X-User-Id", "10")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(likeService).unlikePost(100L, 10L);
    }

    @Test
    @DisplayName("DELETE /api/interactions/likes/{postId} - like not found returns 404")
    void unlikePost_notFound_returns404() throws Exception {
        doThrow(new IllegalStateException("Like not found"))
                .when(likeService).unlikePost(100L, 10L);

        mockMvc.perform(delete("/api/interactions/likes/100")
                        .header("X-User-Id", "10")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/interactions/likes/post/{postId} - returns list of likes")
    void getLikes_returnsList() throws Exception {
        when(likeService.getLikes(100L)).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/api/interactions/likes/post/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].postId").value(100));
    }

    @Test
    @DisplayName("GET /api/interactions/likes/check/{postId} - returns hasLiked and likeCount")
    void checkUserLiked_returnsLikeInfo() throws Exception {
        when(likeService.hasUserLiked(100L, 10L)).thenReturn(true);
        when(likeService.getLikeCount(100L)).thenReturn(5L);

        mockMvc.perform(get("/api/interactions/likes/check/100")
                        .header("X-User-Id", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasLiked").value(true))
                .andExpect(jsonPath("$.likeCount").value(5));
    }
}
