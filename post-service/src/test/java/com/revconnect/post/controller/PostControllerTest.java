package com.revconnect.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revconnect.post.dto.*;
import com.revconnect.post.service.PostService;
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
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("PostController Web Layer Tests")
class PostControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean PostService postService;

    @Test
    @DisplayName("POST /api/posts - creates post returns 201 with post body")
    void createPost_returns201() throws Exception {
        CreatePostRequest req = CreatePostRequest.builder().content("Hello world").build();
        PostResponse response = PostResponse.builder()
                .id(1L).userId(10L).content("Hello world")
                .likeCount(0L).commentCount(0L)
                .createdAt(LocalDateTime.now()).build();

        when(postService.createPost(eq(10L), any(CreatePostRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/posts")
                        .header("X-User-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Hello world"));
    }

    @Test
    @DisplayName("POST /api/posts - empty content returns 400")
    void createPost_emptyContent_returns400() throws Exception {
        String body = "{\"content\":\"\"}";

        mockMvc.perform(post("/api/posts")
                        .header("X-User-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/posts/{id} - returns 200 with post")
    void getPost_returns200() throws Exception {
        PostResponse response = PostResponse.builder()
                .id(1L).userId(10L).content("Content")
                .likeCount(2L).commentCount(1L)
                .createdAt(LocalDateTime.now()).build();

        when(postService.getPost(1L)).thenReturn(response);

        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.likeCount").value(2));
    }

    @Test
    @DisplayName("GET /api/posts/user/{userId} - returns paginated feed")
    void getUserPosts_returnsFeed() throws Exception {
        PostResponse p = PostResponse.builder().id(1L).userId(5L).content("Post").build();
        FeedResponse feed = FeedResponse.builder()
                .posts(List.of(p)).currentPage(0).totalPages(1).totalElements(1).pageSize(20).build();

        when(postService.getUserPosts(eq(5L), eq(0), eq(20))).thenReturn(feed);

        mockMvc.perform(get("/api/posts/user/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("DELETE /api/posts/{id} - returns 204")
    void deletePost_returns204() throws Exception {
        mockMvc.perform(delete("/api/posts/1")
                        .header("X-User-Id", "10")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
