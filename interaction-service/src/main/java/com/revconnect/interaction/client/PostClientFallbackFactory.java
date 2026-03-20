package com.revconnect.interaction.client;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PostClientFallbackFactory implements FallbackFactory<PostClient> {

    @Override
    public PostClient create(Throwable cause) {
        return postId -> Map.of(
            "id", postId,
            "error", "Post service unavailable: " + cause.getMessage()
        );
    }
}
