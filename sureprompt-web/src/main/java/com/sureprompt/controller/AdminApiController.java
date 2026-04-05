package com.sureprompt.controller;

import com.sureprompt.security.SecurityUtils;
import com.sureprompt.service.AdminService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminApiController {

    private final AdminService adminService;

    @PostMapping("/reports/{id}/resolve")
    public ResponseEntity<?> resolveReport(@PathVariable Long id, @RequestBody ResolutionRequest req) {
        Long adminId = SecurityUtils.getCurrentUserId();
        adminService.resolveReport(id, req.getAction(), adminId, req.getNote());
        return ResponseEntity.ok(Map.of("message", "Report resolved successfully"));
    }

    @PostMapping("/reports/{id}/reject")
    public ResponseEntity<?> rejectReport(@PathVariable Long id, @RequestBody Map<String, String> req) {
        Long adminId = SecurityUtils.getCurrentUserId();
        adminService.rejectReport(id, adminId, req.get("note"));
        return ResponseEntity.ok(Map.of("message", "Report rejected"));
    }

    @PostMapping("/users/{id}/ban")
    public ResponseEntity<?> toggleBan(@PathVariable Long id) {
        adminService.toggleUserBan(id);
        return ResponseEntity.ok(Map.of("message", "User ban status toggled"));
    }

    @Data
    public static class ResolutionRequest {
        private String action; // BAN_USER, DELETE_PROMPT, etc.
        private String note;
    }
}
