package com.sureprompt.controller;

import com.sureprompt.dto.UserProfileDto;
import com.sureprompt.security.CustomOAuth2User;
import com.sureprompt.service.FeedService;
import com.sureprompt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FeedService feedService;

    @GetMapping("/users/{username}")
    public String userProfile(@PathVariable String username, Model model, @AuthenticationPrincipal CustomOAuth2User user) {
        Long currentUserId = user != null ? user.getId() : null;
        
        UserProfileDto profile = userService.getUserProfile(username, currentUserId);
        
        model.addAttribute("profile", profile);
        model.addAttribute("userPrompts", feedService.getUserPrompts(profile.getUserId(), 0, currentUserId));
        
        if (user != null) {
            model.addAttribute("currentUser", user.getDatabaseUser());
        }
        
        return "profile";
    }
}
