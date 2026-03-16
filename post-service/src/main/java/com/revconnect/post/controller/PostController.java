package com.revconnect.post.controller;

import com.revconnect.post.dto.CreatePostRequest;
import com.revconnect.post.dto.FeedResponse;
import com.revconnect.post.dto.PostResponse;
import com.revconnect.post.dto.UpdatePostRequest;
import com.revconnect.post.service.PostService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private static final Logger log = LoggerFactory.getLogger(PostController.class);

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreatePostRequest request) {
        log.info("Creating post for userId={}", userId);
        PostResponse response = postService.createPost(userId, request);
        log.info("Post created: postId={}, userId={}", response.getId(), userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long postId,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UpdatePostRequest request) {
        log.info("Updating postId={} by userId={}", postId, userId);
        PostResponse response = postService.updatePost(postId, userId, request);
        log.info("Post updated: postId={}", postId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @RequestHeader("X-User-Id") Long userId) {
        log.info("Deleting postId={} by userId={}", postId, userId);
        postService.deletePost(postId, userId);
        log.info("Post deleted: postId={}", postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(
            @PathVariable Long postId,
            @RequestHeader(value = "X-User-Id", required = false) Long currentUserId) {
        log.debug("Fetching postId={}", postId);
        PostResponse response = postService.getPost(postId, currentUserId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<FeedResponse> getUserPosts(
            @PathVariable Long userId,
            @RequestHeader(value = "X-User-Id", required = false) Long currentUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.debug("Fetching posts for userId={}, page={}, size={}", userId, page, size);
        FeedResponse response = postService.getUserPosts(userId, currentUserId, page, size);
        log.debug("Returned {} posts for userId={}", response.getPosts() != null ? response.getPosts().size() : 0, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/feed")
    public ResponseEntity<FeedResponse> getFeed(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.debug("Fetching feed for userId={}, page={}, size={}", userId, page, size);
        FeedResponse response = postService.getFeed(userId, page, size);
        return ResponseEntity.ok(response);
    }
}
