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
    public Map<String, Object> toggleLike(Long userId, Long promptId) {
        Prompt prompt = promptRepository.findById(promptId)
                .orElseThrow(() -> new RuntimeException("Prompt not found"));
        
        boolean isLiked;
        if (likeRepository.existsByUserIdAndPromptId(userId, promptId)) {
            likeRepository.deleteByUserIdAndPromptId(userId, promptId);
            prompt.setLikeCount(Math.max(0, prompt.getLikeCount() - 1));
            isLiked = false;
        } else {
            User user = userRepository.findById(userId).orElseThrow();
            Like like = Like.builder()
                    .user(user)
                    .prompt(prompt)
                    .build();
            likeRepository.save(like);
            prompt.setLikeCount(prompt.getLikeCount() + 1);
            isLiked = true;
        }
        
        promptRepository.save(prompt);

        Map<String, Object> response = new HashMap<>();
        response.put("liked", isLiked);
        response.put("likeCount", prompt.getLikeCount());
        return response;
    }
}
