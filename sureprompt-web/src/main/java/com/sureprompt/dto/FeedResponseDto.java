package com.sureprompt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedResponseDto {
    private List<PromptCardDto> content;
    private int totalPages;
    private int currentPage;
    private long totalElements;
    private boolean last;
}
