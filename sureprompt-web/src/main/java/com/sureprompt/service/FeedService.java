package com.sureprompt.service;

import com.sureprompt.dto.FeedResponseDto;
import com.sureprompt.dto.PromptCardDto;
import com.sureprompt.entity.Prompt;
import com.sureprompt.repository.LikeRepository;
import com.sureprompt.repository.PromptRepository;
import com.sureprompt.repository.SaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final PromptRepository promptRepository;
    private final LikeRepository likeRepository;
    private final SaveRepository saveRepository;

    @Transactional(readOnly = true)
    public FeedResponseDto getAllFeed(int page, Long currentUserId) {
        Pageable pageable = PageRequest.of(page, 20);
        Page<Prompt> promptPage = promptRepository.findAllFeed(pageable);
        return mapToFeedResponse(promptPage, currentUserId);
    }

    @Transactional(readOnly = true)
    public FeedResponseDto getFollowingFeed(Long userId, int page) {
        Pageable pageable = PageRequest.of(page, 20);
        Page<Prompt> promptPage = promptRepository.findFollowingFeed(userId, pageable);
        return mapToFeedResponse(promptPage, userId);
    }

    @Transactional(readOnly = true)
    public FeedResponseDto getTrendingFeed(int page, Long currentUserId) {
        Pageable pageable = PageRequest.of(page, 20);
        java.time.LocalDateTime since = java.time.LocalDateTime.now().minusDays(7);
        Page<Prompt> promptPage = promptRepository.findTrending(since, pageable);
        return mapToFeedResponse(promptPage, currentUserId);
    }

    public FeedResponseDto getUserPrompts(Long userId, int page, Long currentUserId) {
        Pageable pageable = PageRequest.of(page, 20);
        Page<Prompt> promptPage = promptRepository.findByUserIdAndDeletedFalseOrderByCreatedAtDesc(userId, pageable);
        return mapToFeedResponse(promptPage, currentUserId);
    }

    private FeedResponseDto mapToFeedResponse(Page<Prompt> promptPage, Long currentUserId) {
        List<PromptCardDto> dtoList = promptPage.getContent().stream()
                .map(prompt -> mapToCardDto(prompt, currentUserId))
                .collect(Collectors.toList());

        return new FeedResponseDto(
                dtoList,
                promptPage.getTotalPages(),
                promptPage.getNumber(),
                promptPage.getTotalElements()
        );
    }

    public PromptCardDto mapToCardDto(Prompt prompt, Long currentUserId) {
        List<String> tags = prompt.getPromptTags().stream()
                .map(pt -> pt.getTag().getName())
                .collect(Collectors.toList());

        boolean isLiked = false;
        boolean isSaved = false;
        if (currentUserId != null) {
            isLiked = likeRepository.existsByUserIdAndPromptId(currentUserId, prompt.getId());
            isSaved = saveRepository.existsByUserIdAndPromptId(currentUserId, prompt.getId());
        }

        return PromptCardDto.builder()
                .id(prompt.getId())
                .title(prompt.getTitle())
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
                .isLiked(isLiked)
                .isSaved(isSaved)
                .createdAt(prompt.getCreatedAt())
                .build();
    }
}
