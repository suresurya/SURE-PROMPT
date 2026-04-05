package com.sureprompt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromptVersionDto {
    private Long id;
    private Integer version;
    private String promptText;
    private String aiOutput;
}
