package com.sureprompt.repository;

import com.sureprompt.entity.Save;
import com.sureprompt.entity.SaveId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaveRepository extends JpaRepository<Save, SaveId> {

    boolean existsByUserIdAndPromptId(Long userId, Long promptId);
    void deleteByUserIdAndPromptId(Long userId, Long promptId);

    @Query("SELECT s FROM Save s JOIN FETCH s.prompt WHERE s.user.id = :userId ORDER BY s.createdAt DESC")
    List<Save> findByUserIdWithPrompts(@Param("userId") Long userId);

    long countByPromptId(Long promptId);
}
