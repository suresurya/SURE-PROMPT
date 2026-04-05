package com.sureprompt.dto;

import com.sureprompt.entity.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private Long id;
    private String type; // "LIKE", "FOLLOW", "COMMENT"
    private Long actorId;
    private String actorName;
    private String actorAvatar;
    private Long promptId;
    private String promptTitle;
    private String message;
    private boolean read;
    private LocalDateTime createdAt;
    private String relativeTime; // "5m ago", "1h ago"
}
