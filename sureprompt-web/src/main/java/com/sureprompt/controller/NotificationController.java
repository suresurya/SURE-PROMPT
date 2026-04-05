package com.sureprompt.controller;

import com.sureprompt.dto.NotificationDto;
import com.sureprompt.security.SecurityUtils;
import com.sureprompt.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadCount() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(Map.of("unreadCount", notificationService.getUnreadCount(userId)));
    }

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getNotifications() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(notificationService.getNotifications(userId));
    }

    @PostMapping("/mark-read")
    public ResponseEntity<?> markAllRead() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        notificationService.markAllRead(userId);
        return ResponseEntity.ok().build();
    }
}
