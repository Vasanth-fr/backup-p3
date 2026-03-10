package com.revconnect.interaction.client;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NotificationClientFallbackFactory implements FallbackFactory<NotificationClient> {

    @Override
    public NotificationClient create(Throwable cause) {
        return notification -> System.err.println(
            "Notification fallback triggered: " + cause.getMessage()
        );
    }
}
