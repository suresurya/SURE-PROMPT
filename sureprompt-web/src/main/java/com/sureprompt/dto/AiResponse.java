package com.sureprompt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiResponse {
    private String responseText;
    private int promptTokens;
    private int completionTokens;
    private int totalTokens;
    private boolean success;
    private String errorMessage;
}
