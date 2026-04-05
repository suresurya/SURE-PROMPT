package com.sureprompt.repository;

import com.sureprompt.entity.RefreshToken;
import com.sureprompt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.expiry < :now")
    void deleteAllExpiredSince(@Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE RefreshToken r SET r.revoked = true WHERE r.user = :user")
    void revokeAllUserTokens(@Param("user") User user);
}
