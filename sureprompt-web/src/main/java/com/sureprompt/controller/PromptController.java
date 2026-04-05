package com.sureprompt.controller;

import com.sureprompt.dto.CreatePromptRequest;
import com.sureprompt.security.SecurityUtils;
import com.sureprompt.service.PromptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class PromptController {

    private final PromptService promptService;

    @GetMapping("/prompts/new")
    public String showForm(Model model) {
        model.addAttribute("prompt", new CreatePromptRequest());
        return "post-prompt";
    }

    @PostMapping("/prompts/new")
    public String createPrompt(@ModelAttribute CreatePromptRequest request, RedirectAttributes redirectAttributes) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) return "redirect:/login";

        promptService.createPrompt(request, userId);
        redirectAttributes.addFlashAttribute("successMessage", "Prompt posted successfully!");
        return "redirect:/";
    }

    @GetMapping("/prompts/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        model.addAttribute("prompt", promptService.getPromptDetail(id, currentUserId));
        return "prompt-detail";
    }
}
