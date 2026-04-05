package com.sureprompt.controller;

import com.sureprompt.security.SecurityUtils;
import com.sureprompt.service.SaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/prompts")
@RequiredArgsConstructor
public class SaveController {

    private final SaveService saveService;

    @PostMapping("/{id}/save")
    public ResponseEntity<?> toggleSave(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean saved = saveService.toggleSave(id, userId);

        return ResponseEntity.ok(Map.of(
                "saved", saved
        ));
    }
}
