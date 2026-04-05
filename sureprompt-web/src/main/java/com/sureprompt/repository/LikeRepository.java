package com.sureprompt.repository;

import com.sureprompt.entity.Like;
import com.sureprompt.entity.LikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, LikeId> {

    boolean existsByUserIdAndPromptId(Long userId, Long promptId);
    long countByPromptId(Long promptId);
    void deleteByUserIdAndPromptId(Long userId, Long promptId);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.prompt.user.id = :userId")
    long countLikesReceivedByUserId(@Param("userId") Long userId);
}
