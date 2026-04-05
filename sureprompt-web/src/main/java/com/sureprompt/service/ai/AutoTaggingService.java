package com.sureprompt.service.ai;

import com.sureprompt.dto.AiRequest;
import com.sureprompt.dto.AiResponse;
import com.sureprompt.entity.AiProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AutoTaggingService {

    private final AiGatewayService aiGatewayService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TaggingResult suggestTags(Long userId, String promptText) {
        String systemMsg = """
            You are a technical prompt tagger.
            Analyze the input prompt and suggest appropriate tags (e.g., Programming Language, Domain, Library) and a difficulty level (EASY, MEDIUM, HARD).
            
            Return ONLY a valid JSON object:
            {
              "tags": ["String", "String", ...],
              "difficulty": "EASY|MEDIUM|HARD"
            }
            """;

        AiRequest request = AiRequest.builder()
                .provider(AiProvider.GEMINI)
                .prompt(systemMsg + "\n\nUserInput:\n" + promptText)
                .temperature(0.2)
                .maxTokens(150)
                .build();

        AiResponse response = aiGatewayService.call(userId, request);

        if (!response.isSuccess()) {
            return new TaggingResult(List.of("AI_ANALYSIS_FAILED"), "MEDIUM");
        }

        try {
            String jsonRaw = response.getResponseText().replaceAll("```json|```", "").trim();
            return objectMapper.readValue(jsonRaw, TaggingResult.class);
        } catch (Exception e) {
            return new TaggingResult(List.of("GENERAL"), "MEDIUM");
        }
    }

    @Data
    public static class TaggingResult {
        private List<String> tags;
        private String difficulty;
        
        public TaggingResult() {}
        public TaggingResult(List<String> tags, String difficulty) {
            this.tags = tags;
            this.difficulty = difficulty;
        }
    }
}
