package com.sureprompt.service;

import com.sureprompt.dto.BadgeDto;
import com.sureprompt.dto.UpdateProfileRequest;
import com.sureprompt.dto.UserProfileDto;
import com.sureprompt.entity.User;
import com.sureprompt.repository.FollowRepository;
import com.sureprompt.repository.LikeRepository;
import com.sureprompt.repository.PromptRepository;
import com.sureprompt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PromptRepository promptRepository;
    private final FollowRepository followRepository;
    private final LikeRepository likeRepository;
    private final BadgeService badgeService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User createOrUpdateOnLogin(String email, String username, String displayName, String avatarUrl, String oauthProvider, String oauthSubject) {
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (user.getAvatarUrl() == null && avatarUrl != null) {
                user.setAvatarUrl(avatarUrl);
            }
            if (user.getPassword() == null) {
                user.setPassword(passwordEncoder.encode("admin123"));
            }
            if (!oauthProvider.equals(user.getOauthProvider())) {
                user.setOauthProvider(oauthProvider);
                user.setOauthSubject(oauthSubject);
            }
            return userRepository.save(user);
        }

        String finalUsername = username;
        int counter = 1;
        while (userRepository.existsByUsername(finalUsername)) {
            finalUsername = username + counter++;
        }

        User newUser = User.builder()
                .email(email)
                .username(finalUsername)
                .displayName(displayName)
                .avatarUrl(avatarUrl)
                .oauthProvider(oauthProvider)
                .oauthSubject(oauthSubject)
                .password(passwordEncoder.encode("admin123"))
                .build();

        return userRepository.save(newUser);
    }

    public UserProfileDto getUserProfile(String username, Long currentUserId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new com.sureprompt.exception.ResourceNotFoundException("User not found: " + username));

        Long profileUserId = user.getId();

        // Aggregated stats — one query each, no lazy-loading collections
        long totalPrompts = promptRepository.countByUserIdAndDeletedFalse(profileUserId);
        long totalLikes = likeRepository.countLikesReceivedByUserId(profileUserId);
        int followersCount = (int) followRepository.countByFollowingId(profileUserId);
        int followingCount = (int) followRepository.countByFollowerId(profileUserId);
        double avgAiScore = promptRepository.findAverageAiScoreByUserId(profileUserId);

        // Social flags
        boolean isOwnProfile = currentUserId != null && currentUserId.equals(profileUserId);
        boolean isFollowing = false;
        if (currentUserId != null && !isOwnProfile) {
            isFollowing = followRepository.existsByFollowerIdAndFollowingId(currentUserId, profileUserId);
        }

        // Dynamic badges
        List<BadgeDto> badges = badgeService.getBadges(profileUserId);

        return UserProfileDto.builder()
                .userId(profileUserId)
                .username(user.getUsername())
                .displayName(user.getDisplayName() != null ? user.getDisplayName() : user.getUsername())
                .college(user.getCollege())
                .bio(user.getBio())
                .avatarUrl(user.getAvatarUrl())
                .streakCount(user.getStreakCount())
                .totalLikes(totalLikes)
                .totalPrompts(totalPrompts)
                .followersCount(followersCount)
                .followingCount(followingCount)
                .averageAiScore(Math.round(avgAiScore * 10.0) / 10.0) // 1 decimal
                .isFollowing(isFollowing)
                .isOwnProfile(isOwnProfile)
                .badges(badges)
                .memberSince(user.getCreatedAt())
                .build();
    }

    @Transactional
    public void updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new com.sureprompt.exception.ResourceNotFoundException("User not found"));

        user.setDisplayName(request.getDisplayName());
        user.setBio(request.getBio());
        user.setCollege(request.getCollege());
        if (request.getAvatarUrl() != null && !request.getAvatarUrl().isEmpty()) {
            user.setAvatarUrl(request.getAvatarUrl());
        }

        userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new com.sureprompt.exception.ResourceNotFoundException("User not found"));
    }
}
