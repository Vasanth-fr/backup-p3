package com.revconnect.notification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revconnect.notification.dto.*;
import com.revconnect.notification.entity.NotificationType;
import com.revconnect.notification.service.NotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("NotificationController Web Layer Tests")
class NotificationControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean NotificationService notificationService;

    private NotificationResponse sampleResponse(boolean isRead) {
        NotificationResponse r = new NotificationResponse();
        r.setId(1L); r.setUserId(10L);
        r.setType(NotificationType.LIKE);
        r.setMessage("Your post was liked");
        r.setIsRead(isRead);
        r.setCreatedAt(LocalDateTime.now());
        return r;
    }

    @Test
    @DisplayName("POST /api/notifications - returns 201 with created notification")
    void createNotification_returns201() throws Exception {
        CreateNotificationRequest req = CreateNotificationRequest.builder()
                .userId(10L).type(NotificationType.LIKE).message("Your post was liked").build();

        when(notificationService.createNotification(any())).thenReturn(sampleResponse(false));

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.message").value("Your post was liked"))
                .andExpect(jsonPath("$.isRead").value(false));
    }

    @Test
    @DisplayName("GET /api/notifications - returns list of notifications for user")
    void getNotifications_returnsList() throws Exception {
        when(notificationService.getNotifications(10L))
                .thenReturn(List.of(sampleResponse(false), sampleResponse(true)));

        mockMvc.perform(get("/api/notifications")
                        .header("X-User-Id", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("PUT /api/notifications/{id}/read - returns 200 with read notification")
    void markAsRead_returns200() throws Exception {
        when(notificationService.markAsRead(1L)).thenReturn(sampleResponse(true));

        mockMvc.perform(put("/api/notifications/1/read")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isRead").value(true));
    }

    @Test
    @DisplayName("PUT /api/notifications/read-all - returns 204")
    void markAllAsRead_returns204() throws Exception {
        mockMvc.perform(put("/api/notifications/read-all")
                        .header("X-User-Id", "10")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(notificationService).markAllAsRead(10L);
    }

    @Test
    @DisplayName("GET /api/notifications/count - returns unread count")
    void getUnreadCount_returnsCount() throws Exception {
        NotificationCountResponse countResp = NotificationCountResponse.builder()
                .total(5L).unread(3L).build();
        when(notificationService.getUnreadCount(10L)).thenReturn(countResp);

        mockMvc.perform(get("/api/notifications/count")
                        .header("X-User-Id", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(5))
                .andExpect(jsonPath("$.unread").value(3));
    }

    @Test
    @DisplayName("DELETE /api/notifications/{id} - returns 204")
    void deleteNotification_returns204() throws Exception {
        mockMvc.perform(delete("/api/notifications/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(notificationService).deleteNotification(1L);
    }
}
