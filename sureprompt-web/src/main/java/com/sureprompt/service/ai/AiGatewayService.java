package com.sureprompt.service.ai;

import com.sureprompt.dto.AiRequest;
import com.sureprompt.dto.AiResponse;
import com.sureprompt.entity.AiProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import java.time.Duration;

import java.util.List;
import java.util.Map;

/**
 * Unified Gateway for all AI Providers.
 * Initially supports Google Gemini 1.5.
 */
@Service
@Slf4j
public class AiGatewayService {

    private final RestTemplate restTemplate;
    private final ApiKeyService apiKeyService;

    public AiGatewayService(RestTemplateBuilder restTemplateBuilder, ApiKeyService apiKeyService) {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(10))
                .build();
        this.apiKeyService = apiKeyService;
    }

    public AiResponse call(Long userId, AiRequest request) {
        String apiKey;
        try {
            apiKeyService.validateAndIncrementUsage(userId, request.getProvider());
            apiKey = apiKeyService.getDecryptedKey(userId, request.getProvider()).orElse(null);
        } catch (Exception e) {
            return AiResponse.builder().success(false).errorMessage(e.getMessage()).build();
        }

        if (apiKey == null) {
            return AiResponse.builder()
                    .success(false)
                    .errorMessage("API Key not found for provider: " + request.getProvider())
                    .build();
        }

        try {
            if (request.getProvider() == AiProvider.GEMINI) {
                return callGemini(apiKey, request);
            }
            // Add OpenAI / Claude cases later
            return AiResponse.builder().success(false).errorMessage("Unsupported provider").build();
        } catch (Exception e) {
            log.error("AI call failed", e);
            return AiResponse.builder().success(false).errorMessage(e.getMessage()).build();
        }
    }

    private AiResponse callGemini(String apiKey, AiRequest request) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/" 
                    + (request.getModel() != null ? request.getModel() : "gemini-1.5-flash") 
                    + ":generateContent?key=" + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Gemini JSON Structure
        Map<String, Object> body = Map.of(
            "contents", List.of(
                Map.of("parts", List.of(
                    Map.of("text", request.getPrompt())
                ))
            ),
            "generationConfig", Map.of(
                "temperature", request.getTemperature(),
                "maxOutputTokens", request.getMaxTokens()
            )
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);

        // Extracting Gemini response (simplified parsing)
        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            String text = (String) parts.get(0).get("text");

            int promptTokens = 0;
            int completionTokens = 0;
            if (response.containsKey("usageMetadata")) {
                Map<String, Object> usage = (Map<String, Object>) response.get("usageMetadata");
                promptTokens = usage.containsKey("promptTokenCount") ? (Integer) usage.get("promptTokenCount") : 0;
                completionTokens = usage.containsKey("candidatesTokenCount") ? (Integer) usage.get("candidatesTokenCount") : 0;
            }

            return AiResponse.builder()
                    .success(true)
                    .responseText(text)
                    .promptTokens(promptTokens)
                    .completionTokens(completionTokens)
                    .totalTokens(promptTokens + completionTokens)
                    .build();
        } catch (Exception e) {
            return AiResponse.builder().success(false).errorMessage("Failed to parse Gemini response").build();
        }
    }
}
