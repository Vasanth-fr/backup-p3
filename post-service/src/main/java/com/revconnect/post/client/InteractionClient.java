package com.revconnect.post.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "interaction-service")
public interface InteractionClient {

    @GetMapping("/api/interactions/post/{postId}/counts")
    Map<String, Long> getInteractionCounts(@PathVariable("postId") Long postId);
}
