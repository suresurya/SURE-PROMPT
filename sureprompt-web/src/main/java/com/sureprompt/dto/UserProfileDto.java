package com.sureprompt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private long totalLikes;
    private long totalPrompts;
    private boolean isFollowing;
    private boolean isOwnProfile;
}
