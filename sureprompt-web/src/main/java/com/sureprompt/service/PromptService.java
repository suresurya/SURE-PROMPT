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
    public void createPrompt(CreatePromptRequest req, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new com.sureprompt.exception.ResourceNotFoundException("User not found"));

        Prompt prompt = new Prompt();
        prompt.setUser(user);
        prompt.setTitle(req.getTitle());
        prompt.setPromptBody(req.getPromptBody());
        prompt.setAiOutput(req.getAiOutput());
        prompt.setDifficulty(req.getDifficulty());
        prompt.setPlatform(req.getPlatform());

        Prompt savedPrompt = promptRepository.save(prompt);

        if (req.getTags() != null && !req.getTags().isEmpty()) {
            for (String tagName : req.getTags()) {
                Tag tag = tagService.getOrCreateTag(tagName);
                PromptTag promptTag = new PromptTag();
                promptTag.setPrompt(savedPrompt);
                promptTag.setTag(tag);
                promptTagRepository.save(promptTag);
            }
        }
    }

    public PromptDetailDto getPromptDetail(Long promptId, Long currentUserId) {
        Prompt prompt = promptRepository.findById(promptId)
                .orElseThrow(() -> new com.sureprompt.exception.ResourceNotFoundException("Prompt not found"));

        if (prompt.isDeleted()) {
            throw new com.sureprompt.exception.ResourceNotFoundException("Prompt has been deleted");
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
                .orElseThrow(() -> new com.sureprompt.exception.ResourceNotFoundException("Prompt not found"));

        if (!prompt.getUser().getId().equals(userId)) {
            throw new com.sureprompt.exception.UnauthorizedException("Not authorized");
        }

        prompt.setDeleted(true);
        promptRepository.save(prompt);
    }

    public List<Prompt> getAllPrompts() {
        return promptRepository.findAll();
    }
}
