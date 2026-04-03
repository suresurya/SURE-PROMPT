package com.sureprompt.service;

import com.sureprompt.entity.Follow;
import com.sureprompt.entity.User;
import com.sureprompt.repository.FollowRepository;
import com.sureprompt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional
    public Map<String, Object> toggleFollow(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new RuntimeException("You cannot follow yourself");
        }

        boolean isFollowing;
        if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            followRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);
            isFollowing = false;
        } else {
            User follower = userRepository.findById(followerId).orElseThrow();
            User following = userRepository.findById(followingId).orElseThrow();

            Follow follow = Follow.builder()
                    .follower(follower)
                    .following(following)
                    .build();
            followRepository.save(follow);
            isFollowing = true;
        }

        long followerCount = followRepository.countByFollowingId(followingId);

        Map<String, Object> response = new HashMap<>();
        response.put("following", isFollowing);
        response.put("followerCount", followerCount);
        return response;
    }
}
