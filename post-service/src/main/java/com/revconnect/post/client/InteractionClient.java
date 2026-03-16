package com.revconnect.post.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "interaction-service")
public interface InteractionClient {

    @GetMapping("/api/interactions/counts/{postId}")
    Map<String, Long> getInteractionCounts(@PathVariable("postId") Long postId);

    @GetMapping("/api/interactions/likes/check/{postId}")
    Map<String, Object> checkUserLiked(
            @PathVariable("postId") Long postId,
            @RequestHeader("X-User-Id") Long userId);
}
