package com.sureprompt.service.ai;

import com.sureprompt.dto.AiRequest;
import com.sureprompt.dto.AiResponse;
import com.sureprompt.entity.AiProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PromptScoringService {

    private final AiGatewayService aiGatewayService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ScoringResult score(Long userId, String promptText) {
        String systemMsg = """
            You are a technical prompt scorer.
            Evaluate the input prompt text on a scale of 1-10 for each criteria:
            1. Clarity: Is the instruction unambiguous?
            2. Specificity: Presence of constraints, examples, context?
            3. Structure: Proper use of instructions, delimiters, formatting?
            4. Usefulness: How likely is it to get a high-quality response?
            
            Return ONLY a valid JSON object:
            {
              "clarity": int,
              "specificity": int,
              "structure": int,
              "usefulness": int,
              "totalScore": int,
              "summary": "Brief explanation of the score"
            }
            """;

        AiRequest request = AiRequest.builder()
                .provider(AiProvider.GEMINI)
                .prompt(systemMsg + "\n\nUserInput:\n" + promptText)
                .temperature(0.1)
                .maxTokens(300)
                .build();

        AiResponse response = aiGatewayService.call(userId, request);

        if (!response.isSuccess()) {
            throw new RuntimeException("Scoring service unavailable");
        }

        try {
            String jsonRaw = response.getResponseText().replaceAll("```json|```", "").trim();
            ScoringResult result = objectMapper.readValue(jsonRaw, ScoringResult.class);
            
            if (result.getTotalScore() < 1 || result.getTotalScore() > 40) {
                throw new IllegalStateException("AI returned invalid total score bounds");
            }
            if (result.getClarity() < 1 || result.getClarity() > 10) throw new IllegalStateException("Invalid clarity score");
            
            return result;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to fully parse AI Score format: " + e.getMessage());
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    public static class ScoringResult {
        private int clarity;
        private int specificity;
        private int structure;
        private int usefulness;
        private int totalScore;
        private String summary;
    }
}
