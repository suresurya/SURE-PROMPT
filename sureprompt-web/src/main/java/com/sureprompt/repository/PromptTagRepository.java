package com.sureprompt.repository;

import com.sureprompt.entity.PromptTag;
import com.sureprompt.entity.PromptTagId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromptTagRepository extends JpaRepository<PromptTag, PromptTagId> {

    List<PromptTag> findByPromptId(Long promptId);

    @Modifying
    @Query("DELETE FROM PromptTag pt WHERE pt.prompt.id = :promptId")
    void deleteByPromptId(@Param("promptId") Long promptId);
}
