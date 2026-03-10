package com.revconnect.interaction.controller;

import com.revconnect.interaction.dto.LikeRequest;
import com.revconnect.interaction.dto.LikeResponse;
import com.revconnect.interaction.service.LikeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/interactions/likes")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping
    public ResponseEntity<LikeResponse> likePost(
            @Valid @RequestBody LikeRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            LikeResponse response = likeService.likePost(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> unlikePost(
            @PathVariable Long postId,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            likeService.unlikePost(postId, userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<LikeResponse>> getLikes(@PathVariable Long postId) {
        List<LikeResponse> likes = likeService.getLikes(postId);
        return ResponseEntity.ok(likes);
    }

    @GetMapping("/check/{postId}")
    public ResponseEntity<Map<String, Object>> checkUserLiked(
            @PathVariable Long postId,
            @RequestHeader("X-User-Id") Long userId) {
        boolean hasLiked = likeService.hasUserLiked(postId, userId);
        Long likeCount = likeService.getLikeCount(postId);

        return ResponseEntity.ok(Map.of(
                "hasLiked", hasLiked,
                "likeCount", likeCount
        ));
    }
}
