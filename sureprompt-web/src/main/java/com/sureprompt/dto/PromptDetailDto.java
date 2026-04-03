package com.sureprompt.dto;

import com.sureprompt.entity.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromptDetailDto {
    private Long id;
    private String title;
    private String promptBody;
    private String aiOutput;
    private String authorName;
    private String authorUsername;
    private String authorAvatar;
    private String college;
    private List<String> tags;
    private Difficulty difficulty;
    private String platform;
    private Integer likeCount;
    private Integer saveCount;
    private Double aiScore;
    private boolean aiVerified;
    private String aiVerificationReason;
    private boolean isLiked;
    private boolean isSaved;
    private boolean isOwnPrompt;
    private List<CommentDto> comments;
    private LocalDateTime createdAt;
}
