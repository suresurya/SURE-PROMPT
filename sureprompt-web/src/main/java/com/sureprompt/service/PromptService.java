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
    private final com.sureprompt.repository.PromptVersionRepository promptVersionRepository;
    private final com.sureprompt.service.ai.PromptAiProcessor promptAiProcessor;

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

        // Save v1 Snapshot BEFORE kicking off async
        promptVersionRepository.save(com.sureprompt.entity.PromptVersion.builder()
                .prompt(savedPrompt)
                .version(1)
                .promptText(savedPrompt.getPromptBody())
                .aiOutput(savedPrompt.getAiOutput())
                .build());

        // Process AI scoring, tagging, and cost in the background 
        promptAiProcessor.processPromptAsync(savedPrompt.getId(), userId, req.getTags());

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

        List<com.sureprompt.dto.PromptVersionDto> versionDtos = promptVersionRepository.findAllByPromptIdOrderByVersionDesc(promptId)
                .stream()
                .map(v -> com.sureprompt.dto.PromptVersionDto.builder()
                        .id(v.getId())
                        .version(v.getVersion())
                        .promptText(v.getPromptText())
                        .aiOutput(v.getAiOutput())
                        .build())
                .collect(Collectors.toList());

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
                .aiStatus(prompt.getAiStatus())
                .isLiked(isLiked)
                .isSaved(isSaved)
                .isOwnPrompt(currentUserId != null && currentUserId.equals(prompt.getUser().getId()))
                .versions(versionDtos)
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

    @Transactional
    public void updatePrompt(Long promptId, CreatePromptRequest req, Long userId) {
        Prompt prompt = promptRepository.findById(promptId)
                .orElseThrow(() -> new com.sureprompt.exception.ResourceNotFoundException("Prompt not found"));

        if (!prompt.getUser().getId().equals(userId)) {
            throw new com.sureprompt.exception.UnauthorizedException("Not authorized");
        }

        // 1. Create Version Snapshot of OLD state before update
        Integer latestVersion = promptVersionRepository.findMaxVersionByPromptId(promptId).orElse(1);
        
        // Update prompt
        prompt.setTitle(req.getTitle());
        prompt.setPromptBody(req.getPromptBody());
        prompt.setAiOutput(req.getAiOutput());
        prompt.setAiStatus("PENDING"); // Reset status for the async processor
        
        Prompt updatedPrompt = promptRepository.save(prompt);

        // Save new version synchronously to prevent race conditions before async evaluates it
        promptVersionRepository.save(com.sureprompt.entity.PromptVersion.builder()
                .prompt(updatedPrompt)
                .version(latestVersion + 1)
                .promptText(updatedPrompt.getPromptBody())
                .aiOutput(updatedPrompt.getAiOutput())
                .build());

        // Kick off async AI Re-processing
        promptAiProcessor.processPromptAsync(updatedPrompt.getId(), userId, null);
    }

    @Transactional
    public void retryAiProcessing(Long promptId, Long userId) {
        Prompt prompt = promptRepository.findById(promptId)
                .orElseThrow(() -> new com.sureprompt.exception.ResourceNotFoundException("Prompt not found"));

        if (!prompt.getUser().getId().equals(userId)) {
            throw new com.sureprompt.exception.UnauthorizedException("Not authorized");
        }

        // Only retry if it actually failed to avoid spamming
        if ("FAILED".equals(prompt.getAiStatus())) {
            prompt.setAiStatus("PENDING");
            promptRepository.save(prompt);
            List<String> tags = prompt.getPromptTags().stream().map(pt -> pt.getTag().getName()).collect(Collectors.toList());
            promptAiProcessor.processPromptAsync(prompt.getId(), userId, tags);
        }
    }

    @Transactional
    public void updateScores(Long promptId) {
        Prompt prompt = promptRepository.findById(promptId).orElseThrow();
        
        double aiPart = (prompt.getAiScore() != null ? prompt.getAiScore() : 0.0) * 0.7;
        double communityPart = (prompt.getLikeCount() + (prompt.getSaveCount() * 2)) * 0.3; // Proper weight for saves vs likes
        double bonus = prompt.isAiVerified() ? 1.0 : 0.0;
        
        prompt.setCommunityScore(aiPart + communityPart + bonus);
        promptRepository.save(prompt);
    }
}
