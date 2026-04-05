package com.sureprompt.service;

import com.sureprompt.dto.NotificationDto;
import com.sureprompt.entity.Notification;
import com.sureprompt.entity.NotificationType;
import com.sureprompt.entity.Prompt;
import com.sureprompt.entity.User;
import com.sureprompt.repository.NotificationRepository;
import com.sureprompt.repository.PromptRepository;
import com.sureprompt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final PromptRepository promptRepository;

    @Transactional
    public void createNotification(Long userId, Long actorId, NotificationType type, Long promptId) {
        // Self-check: No notifications for own actions
        if (userId == null || actorId == null || userId.equals(actorId)) {
            return;
        }

        Notification notification = Notification.builder()
                .userId(userId)
                .actorId(actorId)
                .type(type)
                .promptId(promptId)
                .read(false)
                .build();

        notificationRepository.save(notification);
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    public List<NotificationDto> getNotifications(Long userId) {
        return notificationRepository.findTop10ByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAllRead(Long userId) {
        notificationRepository.markAllAsRead(userId);
    }

    private NotificationDto convertToDto(Notification n) {
        User actor = userRepository.findById(n.getActorId()).orElse(null);
        Prompt prompt = n.getPromptId() != null ? promptRepository.findById(n.getPromptId()).orElse(null) : null;

        String actorName = actor != null ? (actor.getDisplayName() != null ? actor.getDisplayName() : actor.getUsername()) : "Someone";
        String promptTitle = prompt != null ? prompt.getTitle() : null;

        return NotificationDto.builder()
                .id(n.getId())
                .type(n.getType().name())
                .actorId(n.getActorId())
                .actorName(actorName)
                .actorAvatar(actor != null ? actor.getAvatarUrl() : null)
                .promptId(n.getPromptId())
                .promptTitle(promptTitle)
                .read(n.isRead())
                .createdAt(n.getCreatedAt())
                .relativeTime(formatRelativeTime(n.getCreatedAt()))
                .build();
    }

    private String formatRelativeTime(LocalDateTime time) {
        if (time == null) return "just now";
        Duration d = Duration.between(time, LocalDateTime.now());
        if (d.toSeconds() < 60) return "just now";
        if (d.toMinutes() < 60) return d.toMinutes() + "m ago";
        if (d.toHours() < 24) return d.toHours() + "h ago";
        if (d.toDays() < 7) return d.toDays() + "d ago";
        return d.toDays() / 7 + "w ago";
    }
}
