package com.sureprompt.controller;

import com.sureprompt.repository.FollowRepository;
import com.sureprompt.security.SecurityUtils;
import com.sureprompt.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;
    private final FollowRepository followRepository;

    @PostMapping("/{id}/follow")
    public ResponseEntity<?> toggleFollow(@PathVariable Long id) {
        Long followerId = SecurityUtils.getCurrentUserId();
        if (followerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean following = followService.toggleFollow(id, followerId);
        long followersCount = followRepository.countByFollowingId(id);

        return ResponseEntity.ok(Map.of(
                "following", following,
                "followersCount", followersCount
        ));
    }
}
