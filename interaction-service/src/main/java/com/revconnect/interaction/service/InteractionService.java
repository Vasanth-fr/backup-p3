package com.revconnect.interaction.service;

import com.revconnect.interaction.dto.InteractionCountResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InteractionService {

    private final LikeService likeService;
    private final CommentService commentService;

    public InteractionService(LikeService likeService, CommentService commentService) {
        this.likeService = likeService;
        this.commentService = commentService;
    }

    public InteractionCountResponse getInteractionCounts(Long postId) {
        return InteractionCountResponse.builder()
                .postId(postId)
                .likeCount(likeService.getLikeCount(postId))
                .commentCount(commentService.getCommentCount(postId))
                .build();
    }

    public List<InteractionCountResponse> getInteractionCountsBatch(List<Long> postIds) {
        return postIds.stream()
                .map(this::getInteractionCounts)
                .collect(Collectors.toList());
    }
}
