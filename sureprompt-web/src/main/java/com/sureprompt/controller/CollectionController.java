package com.sureprompt.controller;

import com.sureprompt.security.CustomOAuth2User;
import com.sureprompt.service.CollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/collections")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    @PostMapping
    public ResponseEntity<?> createCollection(@RequestBody Map<String, Object> payload, @AuthenticationPrincipal CustomOAuth2User user) {
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        
        String name = (String) payload.get("name");
        boolean isPublic = (Boolean) payload.getOrDefault("isPublic", true);
        
        return ResponseEntity.ok(collectionService.createCollection(user.getId(), name, isPublic));
    }

    @PostMapping("/{collectionId}/prompts/{promptId}")
    public ResponseEntity<?> addToCollection(@PathVariable Long collectionId, @PathVariable Long promptId, @AuthenticationPrincipal CustomOAuth2User user) {
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        
        try {
            collectionService.addPromptToCollection(collectionId, promptId, user.getId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
