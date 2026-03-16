package com.revconnect.auth.service;

import com.revconnect.auth.dto.*;
import com.revconnect.auth.entity.RefreshToken;
import com.revconnect.auth.entity.User;
import com.revconnect.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private RefreshTokenService refreshTokenService;
    @Mock private RestTemplate restTemplate;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private RefreshToken testRefreshToken;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L).email("alice@example.com")
                .username("alice")
                .password("encoded-password")
                .firstName("Alice").lastName("Smith")
                .accountType("PERSONAL")
                .build();

        testRefreshToken = RefreshToken.builder()
                .userId(1L).token("refresh-uuid-token")
                .expiryDate(Instant.now().plusSeconds(86400))
                .build();
    }

    // ── Register ──────────────────────────────────────────────────────

    @Test
    @DisplayName("register - happy path creates user and returns tokens")
    void register_happyPath_returnsAuthResponse() {
        RegisterRequest req = RegisterRequest.builder()
                .email("alice@example.com").password("password1")
                .firstName("Alice").lastName("Smith")
                .username("alice").accountType("PERSONAL").build();

        when(userRepository.existsByEmail(req.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(req.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(req.getPassword())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(anyLong(), anyString())).thenReturn("access-token");
        when(refreshTokenService.createRefreshToken(anyLong())).thenReturn(testRefreshToken);
        when(restTemplate.postForEntity(anyString(), any(), eq(Void.class))).thenReturn(null);

        AuthResponse response = authService.register(req);

        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-uuid-token");
        assertThat(response.getEmail()).isEqualTo("alice@example.com");
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("register - duplicate email throws RuntimeException")
    void register_duplicateEmail_throwsException() {
        RegisterRequest req = RegisterRequest.builder()
                .email("alice@example.com").password("pw").firstName("A").lastName("S")
                .username("alice").accountType("PERSONAL").build();

        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("already registered");

        verify(userRepository, never()).save(any());
    }

    // ── Login ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("login - correct credentials return tokens")
    void login_correctCredentials_returnsAuthResponse() {
        LoginRequest req = LoginRequest.builder()
                .identifier("alice@example.com").password("password1").build();

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password1", "encoded-password")).thenReturn(true);
        when(jwtService.generateToken(1L, "alice@example.com")).thenReturn("access-token");
        when(refreshTokenService.createRefreshToken(1L)).thenReturn(testRefreshToken);
        when(restTemplate.postForEntity(anyString(), any(), eq(Void.class))).thenReturn(null);

        AuthResponse response = authService.login(req);

        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getUserId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("login - unknown identifier throws RuntimeException")
    void login_unknownIdentifier_throwsException() {
        LoginRequest req = LoginRequest.builder()
                .identifier("nobody@example.com").password("pw").build();

        when(userRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid email or password");
    }

    @Test
    @DisplayName("login - wrong password throws RuntimeException")
    void login_wrongPassword_throwsException() {
        LoginRequest req = LoginRequest.builder()
                .identifier("alice").password("wrong").build();

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrong", "encoded-password")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid email or password");
    }

    @Test
    @DisplayName("login - username works the same as email")
    void login_username_returnsAuthResponse() {
        LoginRequest req = LoginRequest.builder()
                .identifier("alice").password("password1").build();

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password1", "encoded-password")).thenReturn(true);
        when(jwtService.generateToken(1L, "alice@example.com")).thenReturn("access-token");
        when(refreshTokenService.createRefreshToken(1L)).thenReturn(testRefreshToken);
        when(restTemplate.postForEntity(anyString(), any(), eq(Void.class))).thenReturn(null);

        AuthResponse response = authService.login(req);

        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getUserId()).isEqualTo(1L);
    }

    // ── Refresh ───────────────────────────────────────────────────────

    @Test
    @DisplayName("refreshToken - valid refresh token returns new access token")
    void refreshToken_validToken_returnsNewAccessToken() {
        TokenRefreshRequest req = new TokenRefreshRequest();
        req.setRefreshToken("refresh-uuid-token");

        when(refreshTokenService.findByToken("refresh-uuid-token")).thenReturn(Optional.of(testRefreshToken));
        when(refreshTokenService.verifyExpiration(testRefreshToken)).thenReturn(testRefreshToken);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(1L, "alice@example.com")).thenReturn("new-access-token");

        TokenRefreshResponse response = authService.refreshToken(req);

        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-uuid-token");
    }

    @Test
    @DisplayName("refreshToken - invalid refresh token throws RuntimeException")
    void refreshToken_invalidToken_throwsException() {
        TokenRefreshRequest req = new TokenRefreshRequest();
        req.setRefreshToken("bad-token");

        when(refreshTokenService.findByToken("bad-token")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refreshToken(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid refresh token");
    }

    // ── Logout ────────────────────────────────────────────────────────

    @Test
    @DisplayName("logout - delegates to refreshTokenService.deleteByUserId")
    void logout_callsDeleteByUserId() {
        authService.logout(1L);
        verify(refreshTokenService).deleteByUserId(1L);
    }
}
