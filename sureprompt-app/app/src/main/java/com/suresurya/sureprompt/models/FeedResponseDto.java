package com.suresurya.sureprompt.models;

import java.util.List;

public class FeedResponseDto {
    private List<PromptDetailDto> prompts;
    private int totalPages;
    private long totalElements;

    // Getters and Setters
    public List<PromptDetailDto> getPrompts() { return prompts; }
    public void setPrompts(List<PromptDetailDto> prompts) { this.prompts = prompts; }
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }
}
