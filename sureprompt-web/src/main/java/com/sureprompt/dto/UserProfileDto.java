package com.sureprompt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    private Long userId;
    private String username;
    private String displayName;
    private String college;
    private String bio;
    private String avatarUrl;
    private Integer streakCount;

    // Stats
    private long totalLikes;
    private long totalPrompts;
    private int followersCount;
    private int followingCount;
    private double averageAiScore;

    // Social flags
    private boolean isFollowing;
    private boolean isOwnProfile;

    // Reputation
    private List<BadgeDto> badges;

    // Meta
    private LocalDateTime memberSince;
}
