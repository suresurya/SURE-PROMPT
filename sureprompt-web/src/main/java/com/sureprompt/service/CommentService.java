package com.sureprompt.service;

import com.sureprompt.dto.CommentDto;
import com.sureprompt.entity.Comment;
import com.sureprompt.entity.Prompt;
import com.sureprompt.entity.User;
import com.sureprompt.repository.CommentRepository;
import com.sureprompt.repository.PromptRepository;
import com.sureprompt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PromptRepository promptRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentDto addComment(Long promptId, String body, Long userId) {
        Prompt prompt = promptRepository.findById(promptId)
                .orElseThrow(() -> new RuntimeException("Prompt not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = Comment.builder()
                .prompt(prompt)
                .user(user)
                .body(body)
                .build();

        comment = commentRepository.save(comment);

        return CommentDto.builder()
                .id(comment.getId())
                .authorName(user.getDisplayName() != null ? user.getDisplayName() : user.getUsername())
                .authorUsername(user.getUsername())
                .avatarUrl(user.getAvatarUrl())
                .body(comment.getBody())
                .createdAt(comment.getCreatedAt())
                .isOwnComment(true)
                .build();
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUser().getId().equals(userId)) {
            // Check if admin? Left for future role based checks
            throw new RuntimeException("Not authorized to delete this comment");
        }

        comment.setDeleted(true);
        commentRepository.save(comment);
    }
    
    public Page<CommentDto> getCommentsByPromptId(Long promptId, Long currentUserId, Pageable pageable) {
        return commentRepository.findByPromptIdAndDeletedFalseOrderByCreatedAtDesc(promptId, pageable)
                .map(comment -> CommentDto.builder()
                        .id(comment.getId())
                        .authorName(comment.getUser().getDisplayName() != null ? comment.getUser().getDisplayName() : comment.getUser().getUsername())
                        .authorUsername(comment.getUser().getUsername())
                        .avatarUrl(comment.getUser().getAvatarUrl())
                        .body(comment.getBody())
                        .createdAt(comment.getCreatedAt())
                        .isOwnComment(currentUserId != null && currentUserId.equals(comment.getUser().getId()))
                        .build());
    }
}
