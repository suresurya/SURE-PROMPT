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
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getPromptBody() { return promptBody; }
    public void setPromptBody(String promptBody) { this.promptBody = promptBody; }
    public String getAiOutput() { return aiOutput; }
    public void setAiOutput(String aiOutput) { this.aiOutput = aiOutput; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public String getAuthorUsername() { return authorUsername; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }
    public String getAuthorAvatar() { return authorAvatar; }
    public void setAuthorAvatar(String authorAvatar) { this.authorAvatar = authorAvatar; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public Double getAiScore() { return aiScore; }
    public void setAiScore(Double aiScore) { this.aiScore = aiScore; }
    public boolean isAiVerified() { return aiVerified; }
    public void setAiVerified(boolean aiVerified) { this.aiVerified = aiVerified; }
    public String getAiVerificationReason() { return aiVerificationReason; }
    public void setAiVerificationReason(String aiVerificationReason) { this.aiVerificationReason = aiVerificationReason; }
    public String getAiStatus() { return aiStatus; }
    public void setAiStatus(String aiStatus) { this.aiStatus = aiStatus; }
    public boolean isLiked() { return isLiked; }
    public void setLiked(boolean isLiked) { this.isLiked = isLiked; }
    public boolean isSaved() { return isSaved; }
    public void setSaved(boolean isSaved) { this.isSaved = isSaved; }
    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }
    public Integer getSaveCount() { return saveCount; }
    public void setSaveCount(Integer saveCount) { this.saveCount = saveCount; }
    public List<PromptVersionDto> getVersions() { return versions; }
    public void setVersions(List<PromptVersionDto> versions) { this.versions = versions; }
}
