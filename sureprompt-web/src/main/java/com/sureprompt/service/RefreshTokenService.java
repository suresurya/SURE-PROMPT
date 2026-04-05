package com.sureprompt.service;

import com.sureprompt.entity.RefreshToken;
import com.sureprompt.entity.User;
import com.sureprompt.repository.RefreshTokenRepository;
import com.sureprompt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public RefreshToken createRefreshToken(Long userId, String tokenStr, LocalDateTime expiry) {
        if (userId == null) throw new IllegalArgumentException("UserId cannot be null");
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(tokenStr);
        refreshToken.setExpiry(expiry);
        refreshToken.setRevoked(false);
        // Save using local variable to fix NonNull warnings
        RefreshToken saved = refreshTokenRepository.save(refreshToken);
        return saved;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public void rotateToken(RefreshToken oldToken, String newTokenStr) {
        // Mark old token as replaced, DO NOT revoke immediately (give a 60s grace period)
        oldToken.setReplacedByToken(newTokenStr);
        // It stays !revoked for a bit. Expiry is its actual expiry or we can rely on a cleanup job
        // The grace logic will throw an error if the old token is used AFTER the grace window 
        // OR we can just revoke the family if a revoked token is used.
        refreshTokenRepository.save(oldToken);
    }

    @Transactional
    public void revokeTokensForUser(Long userId) {
        if (userId == null) throw new IllegalArgumentException("UserId cannot be null");
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        refreshTokenRepository.revokeAllUserTokens(user);
    }
    
    @Transactional
    public void revokeToken(RefreshToken token) {
        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }
}
