package com.sureprompt.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "prompt_tags")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@IdClass(PromptTagId.class)
public class PromptTag {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prompt_id", nullable = false)
    private Prompt prompt;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;
}
