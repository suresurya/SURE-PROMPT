package com.sureprompt.controller;

import com.sureprompt.security.CustomOAuth2User;
import com.sureprompt.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/prompts/{id}/like")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<?> toggleLike(@PathVariable Long id, @AuthenticationPrincipal CustomOAuth2User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Must be logged in"));
        }
        return ResponseEntity.ok(likeService.toggleLike(user.getId(), id));
    }
}
