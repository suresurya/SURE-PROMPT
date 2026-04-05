package com.sureprompt.controller;

import com.sureprompt.dto.UpdateProfileRequest;
import com.sureprompt.security.CustomOAuth2User;
import com.sureprompt.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    @GetMapping("/profile/edit")
    public String editProfile(Model model, @AuthenticationPrincipal CustomOAuth2User user) {
        if (user == null) return "redirect:/login";
        
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setDisplayName(user.getDatabaseUser().getDisplayName());
        request.setBio(user.getDatabaseUser().getBio());
        request.setCollege(user.getDatabaseUser().getCollege());
        
        model.addAttribute("updateProfileRequest", request);
        model.addAttribute("currentUser", user.getDatabaseUser());
        
        return "profile-edit";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@Valid @ModelAttribute UpdateProfileRequest request, BindingResult result, 
                              @AuthenticationPrincipal CustomOAuth2User user, RedirectAttributes redirectAttributes, Model model) {
        if (user == null) return "redirect:/login";
        
        if (result.hasErrors()) {
            model.addAttribute("currentUser", user.getDatabaseUser());
            return "profile-edit";
        }
        
        userService.updateProfile(user.getUserId(), request);
        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully");
        
        return "redirect:/users/" + user.getName();
    }
}
