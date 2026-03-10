package com.revconnect.interaction.client;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UserClientFallbackFactory implements FallbackFactory<UserClient> {

    @Override
    public UserClient create(Throwable cause) {
        return userId -> Map.of(
            "id", userId,
            "username", "Unknown User",
            "error", "User service unavailable: " + cause.getMessage()
        );
    }
}
