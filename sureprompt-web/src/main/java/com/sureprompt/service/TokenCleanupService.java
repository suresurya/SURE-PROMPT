package com.sureprompt.service;

import com.sureprompt.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "0 0 * * * *") // Runs every hour at minute 0
    @Transactional
    public void deleteExpiredTokens() {
        log.info("Running scheduled RefreshToken cleanup job...");
        LocalDateTime now = LocalDateTime.now();
        try {
            refreshTokenRepository.deleteAllExpiredSince(now);
            log.info("Successfully cleaned up expired refresh tokens.");
        } catch (Exception e) {
            log.error("Failed to clean up expired refresh tokens", e);
        }
    }
}
