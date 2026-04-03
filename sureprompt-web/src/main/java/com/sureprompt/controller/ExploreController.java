package com.sureprompt.controller;

import com.sureprompt.dto.SearchResponseDto;
import com.sureprompt.security.CustomOAuth2User;
import com.sureprompt.service.SearchService;
import com.sureprompt.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class ExploreController {

    private final SearchService searchService;
    private final TagService tagService;

    @GetMapping("/explore")
    public String explorePage(Model model, @AuthenticationPrincipal CustomOAuth2User user) {
        if (user != null) {
            model.addAttribute("currentUser", user.getDatabaseUser());
        }
        model.addAttribute("availableTags", tagService.getAllActiveTags());
        return "explore";
    }

    @GetMapping("/api/search")
    @ResponseBody
    public SearchResponseDto searchApi(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String platform,
            @RequestParam(defaultValue = "false") boolean verifiedOnly,
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal CustomOAuth2User user) {
            
        Long userId = user != null ? user.getId() : null;
        return searchService.searchPrompts(q, tags, difficulty, platform, verifiedOnly, page, userId);
    }
}
