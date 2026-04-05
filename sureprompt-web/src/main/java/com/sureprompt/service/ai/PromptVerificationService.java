package com.sureprompt.service.ai;

import com.sureprompt.dto.AiRequest;
import com.sureprompt.dto.AiResponse;
import com.sureprompt.entity.AiProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PromptVerificationService {

    private final AiGatewayService aiGatewayService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public VerificationResult verify(Long userId, String promptText) {
        String systemMsg = """
            You are a technical prompt validator for CS students.
            Validate the following prompt based on:
            1. Safety: No malicious, illegal or abusive content.
            2. Utility: Is it a meaningful prompt for AI (e.g. coding, logic, explanation)?
            3. Clarity: Is it understandable?
            
            Return ONLY a valid JSON object:
            {
              "verified": boolean,
              "reason": "String explanation (teaching-focused, suggest improvements if failed)"
            }
            """;

        AiRequest request = AiRequest.builder()
                .provider(AiProvider.GEMINI)
                .prompt(systemMsg + "\n\nUserInput:\n" + promptText)
                .temperature(0.2) // Low temp for consistency
                .maxTokens(200)
                .build();

        AiResponse response = aiGatewayService.call(userId, request);

        if (!response.isSuccess()) {
            throw new RuntimeException("Verification service unavailable: " + response.getErrorMessage());
        }

        try {
            // Remove markdown code blocks if AI included them
            String jsonRaw = response.getResponseText().replaceAll("```json|```", "").trim();
            VerificationResult result = objectMapper.readValue(jsonRaw, VerificationResult.class);
            if (result.getReason() == null || result.getReason().trim().isEmpty()) {
                throw new IllegalStateException("Verification AI returned missing or invalid reason");
            }
            // Pass cost info via reason temporarily if needed, or handle in Gateway
            return result;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse AI Verification format: " + e.getMessage());
        }
    }

    @Data
    public static class VerificationResult {
        private boolean verified;
        private String reason;
        
        public VerificationResult() {}
        public VerificationResult(boolean verified, String reason) {
            this.verified = verified;
            this.reason = reason;
        }
    }
}
