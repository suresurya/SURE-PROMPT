package com.sureprompt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponseDto {
    private List<PromptCardDto> results;
    private int totalPages;
    private int currentPage;
    private long totalResults;
    private String appliedFilters;
}
