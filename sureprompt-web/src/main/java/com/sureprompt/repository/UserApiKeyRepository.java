package com.sureprompt.repository;

import com.sureprompt.entity.AiProvider;
import com.sureprompt.entity.UserApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserApiKeyRepository extends JpaRepository<UserApiKey, Long> {

    Optional<UserApiKey> findByUserIdAndProvider(Long userId, AiProvider provider);
    void deleteByUserIdAndProvider(Long userId, AiProvider provider);
    boolean existsByUserIdAndProvider(Long userId, AiProvider provider);
}
