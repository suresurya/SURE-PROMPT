package com.sureprompt.service;

import com.sureprompt.entity.Like;
import com.sureprompt.entity.Prompt;
import com.sureprompt.entity.User;
import com.sureprompt.repository.LikeRepository;
import com.sureprompt.repository.PromptRepository;
import com.sureprompt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PromptRepository promptRepository;
    private final UserRepository userRepository;

    @Transactional
    public boolean toggleLike(Long promptId, Long userId) {
        boolean exists = likeRepository.existsByUserIdAndPromptId(userId, promptId);
        Prompt prompt = promptRepository.findById(promptId).orElseThrow();
        
        if (exists) {
            likeRepository.deleteByUserIdAndPromptId(userId, promptId);
            prompt.setLikeCount(Math.max(0, prompt.getLikeCount() - 1));
            return false;
        } else {
            User user = userRepository.findById(userId).orElseThrow();
            likeRepository.save(Like.builder().user(user).prompt(prompt).build());
            prompt.setLikeCount(prompt.getLikeCount() + 1);
            return true;
        }
    }

    public int getLikeCount(Long promptId) {
        Prompt prompt = promptRepository.findById(promptId).orElseThrow();
        return prompt.getLikeCount();
    }
}
