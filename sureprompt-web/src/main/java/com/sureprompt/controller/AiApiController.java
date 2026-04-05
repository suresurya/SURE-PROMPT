package com.sureprompt.controller;

import com.sureprompt.dto.AiRequest;
import com.sureprompt.dto.RunRequest;
import com.sureprompt.entity.AiProvider;
import com.sureprompt.security.SecurityUtils;
import com.sureprompt.service.ai.AiGatewayService;
import com.sureprompt.service.ai.ApiKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Slf4j
public class AiApiController {

    private final AiGatewayService aiGatewayService;
    private final ApiKeyService apiKeyService;

    @PostMapping("/live-try")
    public ResponseEntity<?> runPromptLive(@RequestBody RunRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated.");
        }

        if (apiKeyService.getDecryptedKey(userId, AiProvider.GEMINI).isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing API Key");
        }

        try {
            // Check rate limits inside the ApiKeyService implicitly, but the RLP indicated a 429 catch.
            // aiGatewayService.call handles checking if over the 50 limit and fails the request.
            AiRequest aiReq = AiRequest.builder()
                    .provider(AiProvider.GEMINI)
                    .prompt(request.getPrompt())
                    .temperature(0.7) // balanced for creative tasks in Live
                    .maxTokens(800)
                    .build();

            var resp = aiGatewayService.call(userId, aiReq);

            if (!resp.isSuccess()) {
                if (resp.getErrorMessage() != null && resp.getErrorMessage().contains("Rate limit exceeded")) {
                    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Daily AI limit reached for your account.");
                }
                if (resp.getErrorMessage() != null && resp.getErrorMessage().contains("API key not valid")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Your Gemini key is invalid. Update it in Settings.");
                }
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp.getErrorMessage());
            }

            return ResponseEntity.ok(resp.getResponseText());
            
        } catch (Exception e) {
            log.error("Live AI Prompt Execution Failed:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("AI Service Error: " + e.getMessage());
        }
    }
}
