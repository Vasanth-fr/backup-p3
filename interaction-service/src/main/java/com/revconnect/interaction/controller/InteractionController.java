package com.revconnect.interaction.controller;

import com.revconnect.interaction.dto.InteractionCountResponse;
import com.revconnect.interaction.service.InteractionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interactions")
public class InteractionController {

    private final InteractionService interactionService;

    public InteractionController(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    @GetMapping("/counts/{postId}")
    public ResponseEntity<InteractionCountResponse> getInteractionCounts(@PathVariable Long postId) {
        InteractionCountResponse response = interactionService.getInteractionCounts(postId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/counts/batch")
    public ResponseEntity<List<InteractionCountResponse>> getInteractionCountsBatch(
            @RequestBody List<Long> postIds) {
        List<InteractionCountResponse> responses = interactionService.getInteractionCountsBatch(postIds);
        return ResponseEntity.ok(responses);
    }
}
