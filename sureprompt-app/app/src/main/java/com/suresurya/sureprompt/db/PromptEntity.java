package com.suresurya.sureprompt.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "prompts")
@androidx.room.TypeConverters(Converters.class)
public class PromptEntity {
    @PrimaryKey
    private Long id;
    private String title;
    private String promptBody;
    private String authorUsername;
    private String authorAvatar;
    private Double aiScore;
    private String aiStatus;
    private Integer likeCount;
    private boolean isLiked;
    private java.util.List<String> tags;

    public PromptEntity(Long id, String title, String promptBody, String authorUsername, 
                        String authorAvatar, Double aiScore, String aiStatus, 
                        Integer likeCount, boolean isLiked, java.util.List<String> tags) {
        this.id = id;
        this.title = title;
        this.promptBody = promptBody;
        this.authorUsername = authorUsername;
        this.authorAvatar = authorAvatar;
        this.aiScore = aiScore;
        this.aiStatus = aiStatus;
        this.likeCount = likeCount;
        this.isLiked = isLiked;
        this.tags = tags;
    }

    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getPromptBody() { return promptBody; }
    public String getAuthorUsername() { return authorUsername; }
    public String getAuthorAvatar() { return authorAvatar; }
    public Double getAiScore() { return aiScore; }
    public String getAiStatus() { return aiStatus; }
    public Integer getLikeCount() { return likeCount; }
    public boolean isLiked() { return isLiked; }
}
