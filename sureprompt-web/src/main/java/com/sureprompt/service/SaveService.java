package com.sureprompt.service;

import com.sureprompt.entity.Prompt;
import com.sureprompt.entity.Save;
import com.sureprompt.entity.User;
import com.sureprompt.repository.PromptRepository;
import com.sureprompt.repository.SaveRepository;
import com.sureprompt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SaveService {

    private final SaveRepository saveRepository;
    private final PromptRepository promptRepository;
    private final UserRepository userRepository;

    @Transactional
    public Map<String, Object> toggleSave(Long userId, Long promptId) {
        Prompt prompt = promptRepository.findById(promptId)
                .orElseThrow(() -> new RuntimeException("Prompt not found"));

        boolean isSaved;
        if (saveRepository.existsByUserIdAndPromptId(userId, promptId)) {
            saveRepository.deleteByUserIdAndPromptId(userId, promptId);
            prompt.setSaveCount(Math.max(0, prompt.getSaveCount() - 1));
            isSaved = false;
        } else {
            User user = userRepository.findById(userId).orElseThrow();
            Save save = Save.builder()
                    .user(user)
                    .prompt(prompt)
                    .build();
            saveRepository.save(save);
            prompt.setSaveCount(prompt.getSaveCount() + 1);
            isSaved = true;
        }

        promptRepository.save(prompt);

        Map<String, Object> response = new HashMap<>();
        response.put("saved", isSaved);
        response.put("saveCount", prompt.getSaveCount());
        return response;
    }
}
