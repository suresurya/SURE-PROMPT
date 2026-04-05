package com.sureprompt.service;

import com.sureprompt.dto.BadgeDto;
import com.sureprompt.repository.FollowRepository;
import com.sureprompt.repository.LikeRepository;
import com.sureprompt.repository.PromptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final PromptRepository promptRepository;
    private final LikeRepository likeRepository;
    private final FollowRepository followRepository;

    /**
     * Dynamically compute badges from real user stats.
     * No DB table — pure logic.
     */
    public List<BadgeDto> getBadges(Long userId) {
        List<BadgeDto> badges = new ArrayList<>();

        long totalPrompts = promptRepository.countByUserIdAndDeletedFalse(userId);
        double avgScore = promptRepository.findAverageAiScoreByUserId(userId);
        long totalLikes = likeRepository.countLikesReceivedByUserId(userId);
        long followers = followRepository.countByFollowingId(userId);
        long recentPosts = promptRepository.countRecentPromptsByUserId(userId, LocalDateTime.now().minusDays(7));

        // Contributor: ≥ 10 prompts
        if (totalPrompts >= 10) {
            badges.add(BadgeDto.builder()
                    .name("Contributor")
                    .icon("fa-trophy")
                    .color("#f59e0b")
                    .build());
        }

        // Quality Creator: avg AI score ≥ 8
        if (avgScore >= 8.0) {
            badges.add(BadgeDto.builder()
                    .name("Quality Creator")
                    .icon("fa-star")
                    .color("#eab308")
                    .build());
        }

        // Popular: ≥ 100 total likes received
        if (totalLikes >= 100) {
            badges.add(BadgeDto.builder()
                    .name("Popular")
                    .icon("fa-fire")
                    .color("#ef4444")
                    .build());
        }

        // Influencer: ≥ 50 followers
        if (followers >= 50) {
            badges.add(BadgeDto.builder()
                    .name("Influencer")
                    .icon("fa-users")
                    .color("#6366f1")
                    .build());
        }

        // On Fire: 5+ posts in the last 7 days
        if (recentPosts >= 5) {
            badges.add(BadgeDto.builder()
                    .name("On Fire")
                    .icon("fa-bolt")
                    .color("#f97316")
                    .build());
        }

        return badges;
    }
}
