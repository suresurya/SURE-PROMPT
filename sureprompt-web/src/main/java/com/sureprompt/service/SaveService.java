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
    public boolean toggleSave(Long promptId, Long userId) {
        boolean exists = saveRepository.existsByUserIdAndPromptId(userId, promptId);
        Prompt prompt = promptRepository.findById(promptId).orElseThrow();
        
        if (exists) {
            saveRepository.deleteByUserIdAndPromptId(userId, promptId);
            prompt.setSaveCount(Math.max(0, prompt.getSaveCount() - 1));
            return false;
        } else {
            User user = userRepository.findById(userId).orElseThrow();
            saveRepository.save(Save.builder().user(user).prompt(prompt).build());
            prompt.setSaveCount(prompt.getSaveCount() + 1);
            return true;
        }
    }
}
