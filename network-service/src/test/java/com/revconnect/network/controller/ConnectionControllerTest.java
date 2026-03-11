package com.revconnect.network.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revconnect.network.dto.ConnectionRequest;
import com.revconnect.network.dto.ConnectionResponse;
import com.revconnect.network.entity.Connection.ConnectionStatus;
import com.revconnect.network.service.ConnectionService;
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
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConnectionController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("ConnectionController Web Layer Tests")
class ConnectionControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean ConnectionService connectionService;

    private ConnectionResponse pendingResponse() {
        return ConnectionResponse.builder()
                .id(1L).userId(1L).connectedUserId(2L)
                .status(ConnectionStatus.PENDING)
                .createdAt(LocalDateTime.now()).build();
    }

    @Test
    @DisplayName("POST /api/network/connect - returns 201 with pending connection")
    void sendConnectionRequest_returns201() throws Exception {
        ConnectionRequest req = ConnectionRequest.builder().connectedUserId(2L).build();
        when(connectionService.sendRequest(eq(1L), any(ConnectionRequest.class))).thenReturn(pendingResponse());

        mockMvc.perform(post("/api/network/connect")
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    @DisplayName("POST /api/network/connect - self-connection returns 400")
    void sendConnectionRequest_selfConnection_returns400() throws Exception {
        ConnectionRequest req = ConnectionRequest.builder().connectedUserId(1L).build();
        when(connectionService.sendRequest(eq(1L), any()))
                .thenThrow(new IllegalArgumentException("Cannot send connection request to yourself"));

        mockMvc.perform(post("/api/network/connect")
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/network/connections/{id}/accept - returns 200 with accepted status")
    void acceptConnectionRequest_returns200() throws Exception {
        ConnectionResponse accepted = ConnectionResponse.builder()
                .id(1L).userId(1L).connectedUserId(2L)
                .status(ConnectionStatus.ACCEPTED).createdAt(LocalDateTime.now()).build();

        when(connectionService.acceptRequest(2L, 1L)).thenReturn(accepted);

        mockMvc.perform(put("/api/network/connections/1/accept")
                        .header("X-User-Id", "2")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }

    @Test
    @DisplayName("PUT /api/network/connections/{id}/reject - returns 200 with rejected status")
    void rejectConnectionRequest_returns200() throws Exception {
        ConnectionResponse rejected = ConnectionResponse.builder()
                .id(1L).userId(1L).connectedUserId(2L)
                .status(ConnectionStatus.REJECTED).createdAt(LocalDateTime.now()).build();

        when(connectionService.rejectRequest(2L, 1L)).thenReturn(rejected);

        mockMvc.perform(put("/api/network/connections/1/reject")
                        .header("X-User-Id", "2")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    @DisplayName("DELETE /api/network/connections/{id} - returns 204")
    void deleteConnection_returns204() throws Exception {
        mockMvc.perform(delete("/api/network/connections/1")
                        .header("X-User-Id", "1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(connectionService).deleteConnection(1L, 1L);
    }

    @Test
    @DisplayName("GET /api/network/connections - returns list of connections")
    void getConnections_returnsList() throws Exception {
        when(connectionService.getConnections(1L)).thenReturn(List.of(pendingResponse()));

        mockMvc.perform(get("/api/network/connections")
                        .header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].userId").value(1));
    }

    @Test
    @DisplayName("GET /api/network/check/{otherUserId} - returns connection status")
    void checkConnection_returnsStatus() throws Exception {
        when(connectionService.checkConnection(1L, 2L))
                .thenReturn(Map.of("connected", true, "status", "ACCEPTED", "connectionId", 1L));

        mockMvc.perform(get("/api/network/check/2")
                        .header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.connected").value(true))
                .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }
}
