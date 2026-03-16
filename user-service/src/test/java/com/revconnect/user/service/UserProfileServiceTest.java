package com.revconnect.user.service;

import com.revconnect.user.dto.*;
import com.revconnect.user.entity.UserProfile;
import com.revconnect.user.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserProfileService Unit Tests")
class UserProfileServiceTest {

    @Mock private UserProfileRepository userProfileRepository;
    @InjectMocks private UserProfileService userProfileService;

    private UserProfile sampleProfile;

    @BeforeEach
    void setUp() {
        sampleProfile = UserProfile.builder()
                .id(1L).email("alice@example.com")
                .firstName("Alice").lastName("Smith")
                .bio("Software engineer").location("NYC")
                .build();
    }

    // ── getProfile ────────────────────────────────────────────────────

    @Test
    @DisplayName("getProfile - existing email returns profile")
    void getProfile_existingEmail_returnsProfile() {
        when(userProfileRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(sampleProfile));

        UserProfileResponse response = userProfileService.getProfile("alice@example.com");

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("alice@example.com");
        assertThat(response.getFirstName()).isEqualTo("Alice");
    }

    @Test
    @DisplayName("getProfile - non-existent email throws RuntimeException")
    void getProfile_nonExistentEmail_throwsException() {
        when(userProfileRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userProfileService.getProfile("nobody@example.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User profile not found");
    }

    // ── getUserById ───────────────────────────────────────────────────

    @Test
    @DisplayName("getUserById - existing id returns user summary")
    void getUserById_existingId_returnsSummary() {
        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(sampleProfile));

        UserSummaryResponse response = userProfileService.getUserById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getFullName()).isEqualTo("Alice Smith");
        assertThat(response.getUsername()).isEqualTo("alice");
    }

    @Test
    @DisplayName("getUserById - non-existent id throws RuntimeException")
    void getUserById_nonExistentId_throwsException() {
        when(userProfileRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userProfileService.getUserById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    // ── syncUser ──────────────────────────────────────────────────────

    @Test
    @DisplayName("syncUser - creates new profile when not found")
    void syncUser_profileNotFound_createsNew() {
        SyncUserRequest req = new SyncUserRequest();
        req.setId(10L); req.setEmail("bob@example.com");
        req.setFirstName("Bob"); req.setLastName("Jones");

        when(userProfileRepository.findById(10L)).thenReturn(Optional.empty());
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(inv -> {
            UserProfile p = inv.getArgument(0);
            p.setId(10L);
            return p;
        });

        UserSummaryResponse response = userProfileService.syncUser(req);

        assertThat(response.getEmail()).isEqualTo("bob@example.com");
        verify(userProfileRepository).save(any(UserProfile.class));
    }

    @Test
    @DisplayName("syncUser - updates existing profile")
    void syncUser_existingProfile_updates() {
        SyncUserRequest req = new SyncUserRequest();
        req.setId(1L); req.setEmail("alice@example.com");
        req.setFirstName("Alicia"); req.setLastName("Smith");

        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(sampleProfile));
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        UserSummaryResponse response = userProfileService.syncUser(req);

        assertThat(response.getFirstName()).isEqualTo("Alicia");
    }

    @Test
    @DisplayName("syncUser - replaces stale email match when auth user id changes")
    void syncUser_existingEmailWithDifferentId_replacesStaleProfile() {
        SyncUserRequest req = new SyncUserRequest();
        req.setId(10L); req.setEmail("alice@example.com");
        req.setFirstName("Alice"); req.setLastName("Smith");

        when(userProfileRepository.findById(10L)).thenReturn(Optional.empty());
        when(userProfileRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(sampleProfile));
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        UserSummaryResponse response = userProfileService.syncUser(req);

        assertThat(response.getId()).isEqualTo(10L);
        verify(userProfileRepository).delete(sampleProfile);
        verify(userProfileRepository).save(any(UserProfile.class));
    }

    // ── updateProfile ─────────────────────────────────────────────────

    @Test
    @DisplayName("updateProfile - updates bio and location")
    void updateProfile_updatesBioAndLocation() {
        UserProfileRequest req = new UserProfileRequest();
        req.setFirstName("Alice"); req.setLastName("Smith");
        req.setBio("New bio"); req.setLocation("LA");

        when(userProfileRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(sampleProfile));
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        UserProfileResponse response = userProfileService.updateProfile("alice@example.com", req);

        assertThat(response.getBio()).isEqualTo("New bio");
        assertThat(response.getLocation()).isEqualTo("LA");
    }

    // ── searchUsers ───────────────────────────────────────────────────

    @Test
    @DisplayName("searchUsers - blank query returns all users")
    void searchUsers_blankQuery_returnsAll() {
        when(userProfileRepository.findAll()).thenReturn(List.of(sampleProfile));

        List<UserSummaryResponse> result = userProfileService.searchUsers("");

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("searchUsers - non-blank query calls searchUsers repository method")
    void searchUsers_query_callsSearchRepository() {
        when(userProfileRepository.searchUsers("alice")).thenReturn(List.of(sampleProfile));

        List<UserSummaryResponse> result = userProfileService.searchUsers("alice");

        assertThat(result).hasSize(1);
        verify(userProfileRepository).searchUsers("alice");
    }
}
