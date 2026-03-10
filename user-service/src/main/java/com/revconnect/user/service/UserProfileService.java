package com.revconnect.user.service;

import com.revconnect.user.dto.UserProfileRequest;
import com.revconnect.user.dto.UserProfileResponse;
import com.revconnect.user.dto.UserSummaryResponse;
import com.revconnect.user.dto.SyncUserRequest;
import com.revconnect.user.entity.UserProfile;
import com.revconnect.user.repository.UserProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(String email) {
        UserProfile profile = userProfileRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User profile not found for email: " + email));
        return UserProfileResponse.fromEntity(profile);
    }

    @Transactional(readOnly = true)
    public UserSummaryResponse getUserById(Long userId) {
        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return UserSummaryResponse.fromEntity(profile);
    }

    @Transactional
    public UserSummaryResponse syncUser(SyncUserRequest request) {
        UserProfile profile = userProfileRepository.findById(request.getId())
                .orElseGet(UserProfile::new);

        profile.setId(request.getId());
        profile.setEmail(request.getEmail());
        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());

        UserProfile savedProfile = userProfileRepository.save(profile);
        return UserSummaryResponse.fromEntity(savedProfile);
    }

    @Transactional
    public UserProfileResponse updateProfile(String email, UserProfileRequest request) {
        UserProfile profile = userProfileRepository.findByEmail(email)
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setEmail(email);
                    return newProfile;
                });

        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setBio(request.getBio());
        profile.setProfilePictureUrl(request.getProfilePictureUrl());
        profile.setLocation(request.getLocation());

        UserProfile savedProfile = userProfileRepository.save(profile);
        return UserProfileResponse.fromEntity(savedProfile);
    }

    @Transactional(readOnly = true)
    public List<UserSummaryResponse> searchUsers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return userProfileRepository.findAll().stream()
                    .map(UserSummaryResponse::fromEntity)
                    .collect(Collectors.toList());
        }

        return userProfileRepository.searchUsers(query).stream()
                .map(UserSummaryResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
