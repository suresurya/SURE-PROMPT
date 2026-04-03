package com.sureprompt.entity;

import java.io.Serializable;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class CollectionPromptId implements Serializable {
    private Long collection;
    private Long prompt;
}
