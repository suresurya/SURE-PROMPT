package com.sureprompt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private String authorName;
    private String authorUsername;
    private String avatarUrl;
    private String body;
    private LocalDateTime createdAt;
    private boolean isOwnComment;
}
