package com.sureprompt.repository;

import com.sureprompt.entity.PromptVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromptVersionRepository extends JpaRepository<PromptVersion, Long> {
    List<PromptVersion> findAllByPromptIdOrderByVersionDesc(Long promptId);
    
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT MAX(pv.version) FROM PromptVersion pv WHERE pv.prompt.id = :promptId")
    Optional<Integer> findMaxVersionByPromptId(Long promptId);
}
