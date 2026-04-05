package com.sureprompt.controller;

import com.sureprompt.security.CustomOAuth2User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@lombok.RequiredArgsConstructor
public class HomeController {

    private final com.sureprompt.service.PromptService promptService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("prompts", promptService.getAllPrompts());
        return "index";
    }
}
