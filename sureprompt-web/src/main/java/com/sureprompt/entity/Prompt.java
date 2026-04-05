package com.sureprompt.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "prompts", indexes = {
    @Index(name = "idx_prompts_user_id", columnList = "user_id"),
    @Index(name = "idx_prompts_created_at", columnList = "created_at"),
    @Index(name = "idx_prompts_like_count", columnList = "like_count")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Prompt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "prompt_body", nullable = false, columnDefinition = "TEXT")
    private String promptBody;

    @Column(name = "ai_output", nullable = false, columnDefinition = "TEXT")
    private String aiOutput;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Difficulty difficulty;

    // Which AI platform generated the output - ChatGPT, Claude, Gemini, etc.
    @Column(length = 50)
    private String platform;

    @Column(name = "like_count")
    @Builder.Default
    private Integer likeCount = 0;

    @Column(name = "save_count")
    @Builder.Default
    private Integer saveCount = 0;

    // AI fields - populated when user has API key connected
    @Column(name = "ai_score")
    private Double aiScore;

    @Column(name = "ai_verified")
    @Builder.Default
    private boolean aiVerified = false;

    @Column(name = "ai_verification_reason", columnDefinition = "TEXT")
    private String aiVerificationReason;

    // AI Processing State
    @Column(name = "ai_status", length = 20)
    @Builder.Default
    private String aiStatus = "PENDING";

    @Column(name = "cost")
    @Builder.Default
    private Double cost = 0.0;

    // Reproducibility & Reproduction
    @Column(name = "model_name", length = 50)
    private String modelName;

    @Column(name = "temperature")
    @Builder.Default
    private Double temperature = 0.7;

    @Column(name = "tokens_used")
    @Builder.Default
    private Integer tokensUsed = 0;

    // Community Feedback
    @Column(name = "community_score")
    @Builder.Default
    private Double communityScore = 0.0;

    @Builder.Default
    private boolean pinned = false;

    @Builder.Default
    private boolean deleted = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "prompt", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PromptTag> promptTags = new ArrayList<>();

    @OneToMany(mappedBy = "prompt", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "prompt", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "prompt", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Save> saves = new ArrayList<>();
}
