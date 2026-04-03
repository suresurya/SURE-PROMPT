package com.sureprompt.repository;

import com.sureprompt.entity.CollectionPrompt;
import com.sureprompt.entity.CollectionPromptId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionPromptRepository extends JpaRepository<CollectionPrompt, CollectionPromptId> {
    boolean existsByCollectionIdAndPromptId(Long collectionId, Long promptId);
    long countByCollectionId(Long collectionId);
    void deleteByCollectionIdAndPromptId(Long collectionId, Long promptId);
}
