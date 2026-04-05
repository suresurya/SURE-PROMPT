package com.sureprompt.service.ai;

import com.sureprompt.entity.Prompt;
import com.sureprompt.entity.PromptTag;
import com.sureprompt.entity.Tag;
import com.sureprompt.repository.PromptRepository;
import com.sureprompt.repository.PromptTagRepository;
import com.sureprompt.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromptAiProcessor {

    private final PromptVerificationService verificationService;
    private final PromptScoringService scoringService;
    private final AutoTaggingService autoTaggingService;
    private final ApiKeyService apiKeyService;
    private final PromptRepository promptRepository;
    private final PromptTagRepository promptTagRepository;
    private final TagService tagService;

    @Async
    @Transactional
    public void processPromptAsync(Long promptId, Long userId, List<String> manualTags) {
        if (promptId == null) return;
        Prompt prompt = promptRepository.findById(promptId).orElse(null);
        if (prompt == null) {
            log.warn("Async AI task aborted: Prompt {} not found", promptId);
            return;
        }

        try {
            // Check if user has an AI key configured, otherwise just mark as completed
            if (apiKeyService.getDecryptedKey(userId, com.sureprompt.entity.AiProvider.GEMINI).isEmpty()) {
                prompt.setAiStatus("COMPLETED");
                promptRepository.save(prompt);
                return;
            }

            // 1. Verify
            var vResult = verificationService.verify(userId, prompt.getPromptBody());
            prompt.setAiVerified(vResult.isVerified());
            prompt.setAiVerificationReason(vResult.getReason());

            // Estimated Cost calculation tracking (simplified based on limits passed, actual should sum usage parsed from API)
            // Ideally we get the AiResponse or pass cost out of the AI Services. 
            // We'll estimate based on average token response for now, or just charge a flat 0.001 per success
            double totalCost = 0.0;

            if (vResult.isVerified()) {
                // 2. Score
                var sResult = scoringService.score(userId, prompt.getPromptBody());
                prompt.setAiScore((double) sResult.getTotalScore());

                // 3. Auto-Tag
                var tResult = autoTaggingService.suggestTags(userId, prompt.getPromptBody());
                
                if (manualTags == null || manualTags.isEmpty()) {
                    try {
                        prompt.setDifficulty(com.sureprompt.entity.Difficulty.valueOf(tResult.getDifficulty().toUpperCase()));
                    } catch (Exception e) {
                        prompt.setDifficulty(com.sureprompt.entity.Difficulty.MEDIUM);
                    }
                    for (String tagName : tResult.getTags()) {
                        Tag tag = tagService.getOrCreateTag(tagName);
                        PromptTag pt = new PromptTag();
                        pt.setPrompt(prompt);
                        pt.setTag(tag);
                        promptTagRepository.save(pt);
                    }
                }
                
                totalCost = 0.005; // Base estimation cost for 3 consecutive calls
            } else {
                totalCost = 0.001; // Cost for single failing verification call
            }

            prompt.setCost(totalCost);
            prompt.setAiStatus("COMPLETED");
            promptRepository.save(prompt);

        } catch (Exception e) {
            log.error("AI Async processing failed for prompt {}: {}", promptId, e.getMessage());
            prompt.setAiStatus("FAILED");
            promptRepository.save(prompt);
        }
    }
}
