package com.sureprompt.service;

import com.sureprompt.dto.CreatePromptRequest;
import com.sureprompt.dto.PromptDetailDto;
import com.sureprompt.entity.Prompt;
import com.sureprompt.entity.PromptTag;
import com.sureprompt.entity.Tag;
import com.sureprompt.entity.User;
import com.sureprompt.repository.LikeRepository;
import com.sureprompt.repository.PromptRepository;
import com.sureprompt.repository.PromptTagRepository;
import com.sureprompt.repository.SaveRepository;
import com.sureprompt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromptService {

    private final PromptRepository promptRepository;
    private final UserRepository userRepository;
    private final PromptTagRepository promptTagRepository;
    private final TagService tagService;
    private final CommentService commentService;
    private final LikeRepository likeRepository;
    private final SaveRepository saveRepository;

    @Transactional
    public Long createPrompt(Long userId, CreatePromptRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Prompt prompt = Prompt.builder()
                .user(user)
                .title(request.getTitle())
                .promptBody(request.getPromptBody())
                .aiOutput(request.getAiOutput())
                .difficulty(request.getDifficulty())
                .platform(request.getPlatform())
                .build();

        prompt = promptRepository.save(prompt);

        for (String tagName : request.getTags()) {
            Tag tag = tagService.getOrCreateTag(tagName);
            PromptTag pt = PromptTag.builder()
                    .prompt(prompt)
                    .tag(tag)
                    .build();
            promptTagRepository.save(pt);
            prompt.getPromptTags().add(pt);
        }
        
        user.setStreakCount(user.getStreakCount() + 1); // Simplistic streak update
        userRepository.save(user);

        return prompt.getId();
    }

    public PromptDetailDto getPromptDetail(Long promptId, Long currentUserId) {
        Prompt prompt = promptRepository.findById(promptId)
                .orElseThrow(() -> new RuntimeException("Prompt not found"));

        if (prompt.isDeleted()) {
            throw new RuntimeException("Prompt has been deleted");
        }

        List<String> tags = prompt.getPromptTags().stream()
                .map(pt -> pt.getTag().getName())
                .collect(Collectors.toList());

        boolean isLiked = false;
        boolean isSaved = false;
        if (currentUserId != null) {
            isLiked = likeRepository.existsByUserIdAndPromptId(currentUserId, promptId);
            isSaved = saveRepository.existsByUserIdAndPromptId(currentUserId, promptId);
        }

        return PromptDetailDto.builder()
                .id(prompt.getId())
                .title(prompt.getTitle())
                .promptBody(prompt.getPromptBody())
                .aiOutput(prompt.getAiOutput())
                .authorName(prompt.getUser().getDisplayName() != null ? prompt.getUser().getDisplayName() : prompt.getUser().getUsername())
                .authorUsername(prompt.getUser().getUsername())
                .authorAvatar(prompt.getUser().getAvatarUrl())
                .college(prompt.getUser().getCollege())
                .tags(tags)
                .difficulty(prompt.getDifficulty())
                .platform(prompt.getPlatform())
                .likeCount(prompt.getLikeCount())
                .saveCount(prompt.getSaveCount())
                .aiScore(prompt.getAiScore())
                .aiVerified(prompt.isAiVerified())
                .aiVerificationReason(prompt.getAiVerificationReason())
                .isLiked(isLiked)
                .isSaved(isSaved)
                .isOwnPrompt(currentUserId != null && currentUserId.equals(prompt.getUser().getId()))
                .createdAt(prompt.getCreatedAt())
                .build();
    }

    @Transactional
    public void deletePrompt(Long promptId, Long userId) {
        Prompt prompt = promptRepository.findById(promptId)
                .orElseThrow(() -> new RuntimeException("Prompt not found"));

        if (!prompt.getUser().getId().equals(userId)) {
            throw new RuntimeException("Not authorized");
        }

        prompt.setDeleted(true);
        promptRepository.save(prompt);
    }
}
