package com.sureprompt.controller;

import com.sureprompt.entity.AiProvider;
import com.sureprompt.security.SecurityUtils;
import com.sureprompt.service.ai.ApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class SettingsController {

    private final ApiKeyService apiKeyService;

    @GetMapping("/settings")
    public String settingsPage(Model model) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return "redirect:/login";
        }

        boolean hasKey = apiKeyService.getDecryptedKey(currentUserId, AiProvider.GEMINI).isPresent();
        model.addAttribute("hasKey", hasKey);
        
        return "settings";
    }

    @PostMapping("/settings/api-key")
    public String saveKey(@RequestParam String key, RedirectAttributes redirectAttributes) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return "redirect:/login";
        }

        if (key == null || key.length() < 20) {
            redirectAttributes.addFlashAttribute("error", "Invalid API Key. Must be at least 20 characters.");
            return "redirect:/settings";
        }

        apiKeyService.saveKey(currentUserId, AiProvider.GEMINI, key);
        redirectAttributes.addFlashAttribute("success", "API Key saved securely.");
        
        return "redirect:/settings";
    }
}
