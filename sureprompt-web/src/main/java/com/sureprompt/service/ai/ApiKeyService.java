package com.sureprompt.service.ai;

import com.sureprompt.entity.AiProvider;
import com.sureprompt.entity.User;
import com.sureprompt.entity.UserApiKey;
import com.sureprompt.repository.UserApiKeyRepository;
import com.sureprompt.repository.UserRepository;
import com.sureprompt.util.EncryptionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final UserApiKeyRepository apiKeyRepository;
    private final UserRepository userRepository;
    private final EncryptionUtils encryptionUtils;

    @Transactional
    public void saveKey(Long userId, AiProvider provider, String rawKey) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String encryptedText = encryptionUtils.encrypt(rawKey);

        UserApiKey apiKey = apiKeyRepository.findByUserIdAndProvider(userId, provider)
                .orElse(UserApiKey.builder()
                        .user(user)
                        .provider(provider)
                        .build());

        apiKey.setEncryptedKey(encryptedText);
        apiKeyRepository.save(apiKey);
    }

    public Optional<String> getDecryptedKey(Long userId, AiProvider provider) {
        return apiKeyRepository.findByUserIdAndProvider(userId, provider)
                .map(key -> encryptionUtils.decrypt(key.getEncryptedKey()));
    }

    @Transactional
    public void validateAndIncrementUsage(Long userId, AiProvider provider) {
        UserApiKey key = apiKeyRepository.findByUserIdAndProvider(userId, provider)
                .orElseThrow(() -> new RuntimeException("API Key not found"));

        LocalDate today = LocalDate.now();
        if (!today.equals(key.getLastCallDate())) {
            key.setDailyCalls(0);
            key.setLastCallDate(today);
        }

        if (key.getDailyCalls() != null && key.getDailyCalls() > 50) {
            throw new RuntimeException("Daily AI limit reached (Max 50)");
        }

        key.setDailyCalls((key.getDailyCalls() == null ? 0 : key.getDailyCalls()) + 1);
        key.setLastUsedAt(LocalDateTime.now());
        apiKeyRepository.save(key);
    }

    @Transactional
    public void deleteKey(Long userId, AiProvider provider) {
        apiKeyRepository.findByUserIdAndProvider(userId, provider)
                .ifPresent(apiKeyRepository::delete);
    }
}
