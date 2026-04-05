package com.sureprompt.controller;

import com.sureprompt.dto.FeedResponseDto;
import com.sureprompt.security.SecurityUtils;
import com.sureprompt.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @GetMapping
    public FeedResponseDto getFeed(
            @RequestParam(defaultValue = "all") String tab,
            @RequestParam(defaultValue = "0") int page
    ) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        
        // For 'following' tab, we strictly need a user. 
        // For others, currentUserId can be null (viewing as guest).
        if ("following".equalsIgnoreCase(tab)) {
            if (currentUserId == null) {
                // Return empty feed or fallback for now
                return new FeedResponseDto(List.of(), 0, page, 0, true);
            }
            return feedService.getFollowingFeed(currentUserId, page);
        } else if ("trending".equalsIgnoreCase(tab)) {
            return feedService.getTrendingFeed(page, currentUserId);
        } else {
            return feedService.getAllFeed(page, currentUserId);
        }
    }
}
