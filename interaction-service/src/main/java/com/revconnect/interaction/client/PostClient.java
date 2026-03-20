package com.revconnect.interaction.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "post-service", fallbackFactory = PostClientFallbackFactory.class)
public interface PostClient {

    @GetMapping("/api/posts/{postId}")
    Map<String, Object> getPost(@PathVariable("postId") Long postId);
}
