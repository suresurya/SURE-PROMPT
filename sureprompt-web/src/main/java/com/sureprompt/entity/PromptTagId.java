package com.sureprompt.entity;

import java.io.Serializable;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class PromptTagId implements Serializable {
    private Long prompt;
    private Long tag;
}
