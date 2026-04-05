package com.sureprompt.service;

import com.sureprompt.entity.Follow;
import com.sureprompt.entity.User;
import com.sureprompt.repository.FollowRepository;
import com.sureprompt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional
    public boolean toggleFollow(Long followingId, Long followerId) {
        if (followerId.equals(followingId)) return false;

        boolean exists = followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
        
        if (exists) {
            followRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);
            return false;
        } else {
            User follower = userRepository.findById(followerId).orElseThrow();
            User following = userRepository.findById(followingId).orElseThrow();
            followRepository.save(Follow.builder().follower(follower).following(following).build());
            return true;
        }
    }
}
