package com.sureprompt.service;

import com.sureprompt.dto.UpdateProfileRequest;
import com.sureprompt.dto.UserProfileDto;
import com.sureprompt.entity.User;
import com.sureprompt.repository.FollowRepository;
import com.sureprompt.repository.LikeRepository;
import com.sureprompt.repository.PromptRepository;
import com.sureprompt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PromptRepository promptRepository;
    private final FollowRepository followRepository;

    @Transactional
    public User createOrUpdateOnLogin(String email, String username, String displayName, String avatarUrl, String oauthProvider, String oauthSubject) {
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            // Update latest info if needed
            if (user.getAvatarUrl() == null && avatarUrl != null) {
                user.setAvatarUrl(avatarUrl);
            }
            if (!oauthProvider.equals(user.getOauthProvider())) {
                user.setOauthProvider(oauthProvider);
                user.setOauthSubject(oauthSubject);
            }
            return userRepository.save(user);
        }

        // Generate unique username if taken
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
                .build();

        return userRepository.save(newUser);
    }

    public UserProfileDto getUserProfile(String username, Long currentUserId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new com.sureprompt.exception.ResourceNotFoundException("User not found: " + username));

        long totalPrompts = promptRepository.countByUserIdAndDeletedFalse(user.getId());
        
        // Sum total likes received across all user's prompts
        // For simplicity, we can do a repository query, or just use a basic sum here if loaded
        long totalLikes = user.getPrompts().stream()
                .filter(p -> !p.isDeleted())
                .mapToInt(p -> p.getLikeCount() != null ? p.getLikeCount() : 0)
                .sum();

        boolean isFollowing = false;
        if (currentUserId != null && !currentUserId.equals(user.getId())) {
            isFollowing = followRepository.existsByFollowerIdAndFollowingId(currentUserId, user.getId());
        }

        boolean isOwnProfile = currentUserId != null && currentUserId.equals(user.getId());

        return UserProfileDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .displayName(user.getDisplayName() != null ? user.getDisplayName() : user.getUsername())
                .college(user.getCollege())
                .bio(user.getBio())
                .avatarUrl(user.getAvatarUrl())
                .streakCount(user.getStreakCount())
                .totalLikes(totalLikes)
                .totalPrompts(totalPrompts)
                .isFollowing(isFollowing)
                .isOwnProfile(isOwnProfile)
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
