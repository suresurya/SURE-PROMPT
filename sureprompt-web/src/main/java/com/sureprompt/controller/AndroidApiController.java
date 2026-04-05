package com.sureprompt.controller;

import com.sureprompt.dto.CreatePromptRequest;
import com.sureprompt.dto.FeedResponseDto;
import com.sureprompt.security.CustomOAuth2User;
import com.sureprompt.service.FeedService;
import com.sureprompt.service.PromptService;
import com.sureprompt.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AndroidApiController {

    private final FeedService feedService;
    private final SearchService searchService;
    private final PromptService promptService;

    // Mobile specific API endpoints that mirror other logic but standardized under /api/v1
    
    @GetMapping("/feed")
    public ResponseEntity<FeedResponseDto> getFeed(
            @RequestParam(defaultValue = "all") String tab,
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal CustomOAuth2User user) {
            
        Long userId = user != null ? user.getUserId() : null;
        
        if ("following".equals(tab) && userId != null) {
            return ResponseEntity.ok(feedService.getFollowingFeed(userId, page));
        } else if ("trending".equals(tab)) {
            return ResponseEntity.ok(feedService.getTrendingFeed(page, userId));
        } else {
            return ResponseEntity.ok(feedService.getAllFeed(page, userId));
        }
    }

    @PostMapping("/prompts")
    public ResponseEntity<?> createPrompt(@RequestBody CreatePromptRequest request, @AuthenticationPrincipal CustomOAuth2User user) {
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        
        try {
            promptService.createPrompt(request, user.getUserId());
            return ResponseEntity.ok().body("Prompt Created Successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/prompts/{id}")
    public ResponseEntity<?> getPrompt(@PathVariable Long id, @AuthenticationPrincipal CustomOAuth2User user) {
        try {
            Long userId = user != null ? user.getUserId() : null;
            return ResponseEntity.ok(promptService.getPromptDetail(id, userId));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
