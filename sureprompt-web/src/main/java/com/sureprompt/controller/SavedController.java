package com.sureprompt.controller;

import com.sureprompt.security.CustomOAuth2User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SavedController {

    @GetMapping("/saved")
    public String savedPage(Model model, @AuthenticationPrincipal CustomOAuth2User user) {
        if (user == null) return "redirect:/login";
        
        model.addAttribute("currentUser", user.getDatabaseUser());
        // Detailed implementation would load collections
        return "saved";
    }
}
