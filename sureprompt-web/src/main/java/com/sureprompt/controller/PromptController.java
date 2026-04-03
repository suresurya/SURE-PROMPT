package com.sureprompt.controller;

import com.sureprompt.dto.CreatePromptRequest;
import com.sureprompt.dto.PromptDetailDto;
import com.sureprompt.security.CustomOAuth2User;
import com.sureprompt.service.CommentService;
import com.sureprompt.service.PromptService;
import com.sureprompt.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/prompts")
@RequiredArgsConstructor
public class PromptController {

    private final PromptService promptService;
    private final TagService tagService;
    private final CommentService commentService;

    @GetMapping("/new")
    public String newPromptForm(Model model, @AuthenticationPrincipal CustomOAuth2User user) {
        if (user == null) return "redirect:/login";
        model.addAttribute("createPromptRequest", new CreatePromptRequest());
        model.addAttribute("availableTags", tagService.getAllActiveTags());
        model.addAttribute("currentUser", user.getDatabaseUser());
        return "post-prompt";
    }

    @PostMapping("/new")
    public String createPrompt(@Valid @ModelAttribute CreatePromptRequest createPromptRequest, BindingResult result, Model model, @AuthenticationPrincipal CustomOAuth2User user, RedirectAttributes redirectAttributes) {
        if (user == null) return "redirect:/login";
        
        if (result.hasErrors()) {
            model.addAttribute("availableTags", tagService.getAllActiveTags());
            model.addAttribute("currentUser", user.getDatabaseUser());
            return "post-prompt";
        }

        Long promptId = promptService.createPrompt(user.getId(), createPromptRequest);
        
        redirectAttributes.addFlashAttribute("successMessage", "Prompt published successfully!");
        return "redirect:/prompts/" + promptId;
    }

    @GetMapping("/{id}")
    public String viewPrompt(@PathVariable Long id, Model model, @AuthenticationPrincipal CustomOAuth2User user) {
        Long userId = user != null ? user.getId() : null;
        PromptDetailDto prompt = promptService.getPromptDetail(id, userId);
        
        prompt.setComments(commentService.getCommentsByPromptId(id, userId, PageRequest.of(0, 50)).getContent());
        
        model.addAttribute("prompt", prompt);
        if (user != null) {
            model.addAttribute("currentUser", user.getDatabaseUser());
        }
        return "prompt-detail";
    }

    @PostMapping("/{id}/delete")
    public String deletePrompt(@PathVariable Long id, @AuthenticationPrincipal CustomOAuth2User user, RedirectAttributes redirectAttributes) {
        if (user == null) return "redirect:/login";
        promptService.deletePrompt(id, user.getId());
        redirectAttributes.addFlashAttribute("successMessage", "Prompt deleted");
        return "redirect:/";
    }
}
