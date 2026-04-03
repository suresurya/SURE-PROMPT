package com.sureprompt.controller;

import com.sureprompt.security.CustomOAuth2User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal CustomOAuth2User user) {
        if (user != null) {
            model.addAttribute("currentUser", user.getDatabaseUser());
        }
        return "index";
    }
}
