package com.sureprompt.dto;

import com.sureprompt.entity.AiProvider;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class AiRequest {
    private AiProvider provider;
    private String model;
    private String prompt;
    private double temperature;
    private int maxTokens;
    private Map<String, Object> additionalParams;
}
