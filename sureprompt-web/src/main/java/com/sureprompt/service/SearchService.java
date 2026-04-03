package com.sureprompt.service;

import com.sureprompt.dto.SearchResponseDto;
import com.sureprompt.entity.Prompt;
import com.sureprompt.repository.PromptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final PromptRepository promptRepository;
    private final FeedService feedService; // Reusing card mapping

    @Transactional(readOnly = true)
    public SearchResponseDto searchPrompts(String query, String tags, String difficulty, String platform, boolean verifiedOnly, int page, Long currentUserId) {
        Pageable pageable = PageRequest.of(page, 20);
        
        // Parse difficulty string to Enum
        com.sureprompt.entity.Difficulty enumDifficulty = null;
        if (difficulty != null && !difficulty.isEmpty()) {
            try {
                enumDifficulty = com.sureprompt.entity.Difficulty.valueOf(difficulty.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Ignore invalid difficulty
            }
        }

        // This is a simplified search. Usually you'd use Specifications or full-text native query.
        Page<Prompt> promptPage = promptRepository.searchWithFilters(
            query != null ? query : "",
            enumDifficulty,
            platform == null || platform.isEmpty() ? null : platform,
            verifiedOnly,
            pageable
        );
        
        SearchResponseDto response = new SearchResponseDto();
        response.setResults(promptPage.getContent().stream().map(p -> feedService.mapToCardDto(p, currentUserId)).toList());
        response.setCurrentPage(promptPage.getNumber());
        response.setTotalPages(promptPage.getTotalPages());
        response.setTotalResults(promptPage.getTotalElements());
        
        StringBuilder filters = new StringBuilder();
        if (difficulty != null) filters.append("Difficulty=").append(difficulty).append(" ");
        if (platform != null) filters.append("Platform=").append(platform).append(" ");
        if (verifiedOnly) filters.append("VerifiedOnly ");
        response.setAppliedFilters(filters.toString().trim());

        return response;
    }
}
