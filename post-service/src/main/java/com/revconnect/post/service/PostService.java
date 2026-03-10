package com.revconnect.post.service;

import com.revconnect.post.client.InteractionClient;
import com.revconnect.post.client.UserClient;
import com.revconnect.post.dto.CreatePostRequest;
import com.revconnect.post.dto.FeedResponse;
import com.revconnect.post.dto.PostResponse;
import com.revconnect.post.dto.UpdatePostRequest;
import com.revconnect.post.entity.Post;
import com.revconnect.post.repository.PostRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final InteractionClient interactionClient;
    private final UserClient userClient;

    public PostService(PostRepository postRepository,
                      InteractionClient interactionClient,
                      UserClient userClient) {
        this.postRepository = postRepository;
        this.interactionClient = interactionClient;
        this.userClient = userClient;
    }

    @Transactional
    public PostResponse createPost(Long userId, CreatePostRequest request) {
        Post post = Post.builder()
                .userId(userId)
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .build();

        Post savedPost = postRepository.save(post);
        return mapToPostResponse(savedPost);
    }

    @Transactional
    public PostResponse updatePost(Long postId, Long userId, UpdatePostRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to update this post");
        }

        if (request.getContent() != null) {
            post.setContent(request.getContent());
        }
        if (request.getImageUrl() != null) {
            post.setImageUrl(request.getImageUrl());
        }

        Post updatedPost = postRepository.save(post);
        return mapToPostResponse(updatedPost);
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this post");
        }

        postRepository.delete(post);
    }

    @Transactional(readOnly = true)
    public PostResponse getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return mapToPostResponseWithCounts(post);
    }

    @Transactional(readOnly = true)
    public FeedResponse getUserPosts(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);

        List<PostResponse> postResponses = postPage.getContent().stream()
                .map(this::mapToPostResponseWithCounts)
                .collect(Collectors.toList());

        return FeedResponse.builder()
                .posts(postResponses)
                .currentPage(postPage.getNumber())
                .totalPages(postPage.getTotalPages())
                .totalElements(postPage.getTotalElements())
                .pageSize(postPage.getSize())
                .build();
    }

    @Transactional(readOnly = true)
    public FeedResponse getFeed(Long userId, int page, int size) {
        // Get list of user IDs that the current user follows
        List<Long> followedUserIds = getFollowedUserIds(userId);

        // Add the current user's ID to see their own posts in the feed
        followedUserIds.add(userId);

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findByUserIdInOrderByCreatedAtDesc(followedUserIds, pageable);

        List<PostResponse> postResponses = postPage.getContent().stream()
                .map(this::mapToPostResponseWithCounts)
                .collect(Collectors.toList());

        return FeedResponse.builder()
                .posts(postResponses)
                .currentPage(postPage.getNumber())
                .totalPages(postPage.getTotalPages())
                .totalElements(postPage.getTotalElements())
                .pageSize(postPage.getSize())
                .build();
    }

    private PostResponse mapToPostResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .createdAt(post.getCreatedAt())
                .likeCount(0L)
                .commentCount(0L)
                .build();
    }

    private PostResponse mapToPostResponseWithCounts(Post post) {
        Map<String, Long> counts = getInteractionCounts(post.getId());

        return PostResponse.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .createdAt(post.getCreatedAt())
                .likeCount(counts.getOrDefault("likeCount", 0L))
                .commentCount(counts.getOrDefault("commentCount", 0L))
                .build();
    }

    @CircuitBreaker(name = "interactionService", fallbackMethod = "getInteractionCountsFallback")
    private Map<String, Long> getInteractionCounts(Long postId) {
        return interactionClient.getInteractionCounts(postId);
    }

    private Map<String, Long> getInteractionCountsFallback(Long postId, Throwable throwable) {
        return Map.of("likeCount", 0L, "commentCount", 0L);
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "getFollowedUserIdsFallback")
    private List<Long> getFollowedUserIds(Long userId) {
        // This would call network-service to get the list of users that the current user follows
        // For now, returning empty list as network-service integration is pending
        return new ArrayList<>();
    }

    private List<Long> getFollowedUserIdsFallback(Long userId, Throwable throwable) {
        return new ArrayList<>();
    }
}
