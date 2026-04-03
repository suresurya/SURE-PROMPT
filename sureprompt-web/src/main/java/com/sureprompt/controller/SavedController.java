package com.sureprompt.controller;

import com.sureprompt.security.CustomOAuth2User;
import com.sureprompt.dto.PromptCardDto;
import com.sureprompt.entity.Collection;
import com.sureprompt.entity.Save;
import com.sureprompt.repository.CollectionRepository;
import com.sureprompt.repository.SaveRepository;
import com.sureprompt.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class SavedController {

    private final SaveRepository saveRepository;
    private final CollectionRepository collectionRepository;
    private final FeedService feedService;

    @GetMapping("/saved")
    public String savedPage(Model model, @AuthenticationPrincipal CustomOAuth2User user) {
        if (user == null) return "redirect:/login";
        
        Long userId = user.getId();
        model.addAttribute("currentUser", user.getDatabaseUser());
        
        List<Collection> collections = collectionRepository.findByUserIdOrderByCreatedAtDesc(userId);
        model.addAttribute("collections", collections);
        
        List<Save> saves = saveRepository.findByUserIdWithPrompts(userId);
        List<PromptCardDto> savedPrompts = saves.stream()
                .map(save -> feedService.mapToCardDto(save.getPrompt(), userId))
                .collect(Collectors.toList());
        model.addAttribute("savedPrompts", savedPrompts);
        
        return "saved";
    }
}
