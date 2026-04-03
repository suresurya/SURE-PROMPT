package com.sureprompt.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "collection_prompts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@IdClass(CollectionPromptId.class)
public class CollectionPrompt {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id", nullable = false)
    private Collection collection;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prompt_id", nullable = false)
    private Prompt prompt;
}
