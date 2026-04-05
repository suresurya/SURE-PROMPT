package com.suresurya.sureprompt.models;

import java.util.List;

public class PromptDetailDto {
    private Long id;
    private String title;
    private String promptBody;
    private String aiOutput;
    private String authorName;
    private String authorUsername;
    private String authorAvatar;
    private List<String> tags;
    private String status;
    private Double aiScore;
    private boolean aiVerified;
    private String aiVerificationReason;
    private String aiStatus;
    private boolean isLiked;
    private boolean isSaved;
    private Integer likeCount;
    private Integer saveCount;
    private List<PromptVersionDto> versions;

    // Getters and Setters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getPromptBody() { return promptBody; }
    public String getAiOutput() { return aiOutput; }
    public String getAuthorName() { return authorName; }
    public String getAuthorUsername() { return authorUsername; }
    public String getAuthorAvatar() { return authorAvatar; }
    public List<String> getTags() { return tags; }
    public Double getAiScore() { return aiScore; }
    public boolean isAiVerified() { return aiVerified; }
    public String getAiVerificationReason() { return aiVerificationReason; }
    public String getAiStatus() { return aiStatus; }
    public boolean isLiked() { return isLiked; }
    public boolean isSaved() { return isSaved; }
    public Integer getLikeCount() { return likeCount; }
    public Integer getSaveCount() { return saveCount; }
    public List<PromptVersionDto> getVersions() { return versions; }
}
