package com.sureprompt.controller;

import com.sureprompt.dto.UserProfileDto;
import com.sureprompt.security.CustomOAuth2User;
import com.sureprompt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getMe(@AuthenticationPrincipal CustomOAuth2User oauthUser) {
        if (oauthUser == null) {
            return ResponseEntity.status(401).build();
        }
        
        UserProfileDto profile = userService.getUserProfile(oauthUser.getDatabaseUser().getUsername(), oauthUser.getUserId());
        return ResponseEntity.ok(profile);
    }
}
