package com.revconnect.network.client;

import com.revconnect.network.dto.ConnectionResponse.UserDetails;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/api/users/{userId}")
    @CircuitBreaker(name = "userService", fallbackMethod = "getUserDetailsFallback")
    UserDetails getUserDetails(@PathVariable("userId") Long userId);

    default UserDetails getUserDetailsFallback(Long userId, Exception e) {
        UserDetails fallbackDetails = new UserDetails();
        fallbackDetails.setId(userId);
        fallbackDetails.setUsername("Unknown User");
        fallbackDetails.setFullName("Unknown User");
        fallbackDetails.setProfileImageUrl(null);
        return fallbackDetails;
    }
}
