package com.sureprompt.controller;

import com.sureprompt.security.SecurityUtils;
import com.sureprompt.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/prompts")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{id}/like")
    public ResponseEntity<?> toggleLike(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean liked = likeService.toggleLike(id, userId);
        int count = likeService.getLikeCount(id);

        return ResponseEntity.ok(Map.of(
                "liked", liked,
                "likeCount", count
        ));
    }
}
