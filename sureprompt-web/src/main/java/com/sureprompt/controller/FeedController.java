package com.sureprompt.controller;

import com.sureprompt.dto.FeedResponseDto;
import com.sureprompt.security.CustomOAuth2User;
import com.sureprompt.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @GetMapping
    public FeedResponseDto getFeed(
            @RequestParam(defaultValue = "all") String tab,
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal CustomOAuth2User user) {
            
        Long userId = user != null ? user.getId() : null;
        
        if ("following".equals(tab) && userId != null) {
            return feedService.getFollowingFeed(userId, page);
        } else if ("trending".equals(tab)) {
            return feedService.getTrendingFeed(page, userId);
        } else {
            return feedService.getAllFeed(page, userId);
        }
    }
}
