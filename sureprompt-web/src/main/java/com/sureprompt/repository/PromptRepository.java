package com.sureprompt.repository;

import com.sureprompt.entity.Prompt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromptRepository extends JpaRepository<Prompt, Long> {

    // All feed - non-deleted, ordered newest first (pinned on top)
    @Query("SELECT p FROM Prompt p WHERE p.deleted = false ORDER BY p.pinned DESC, p.createdAt DESC")
    Page<Prompt> findAllFeed(Pageable pageable);

    // Following feed - only from users the current user follows
    @Query("""
        SELECT p FROM Prompt p 
        WHERE p.deleted = false 
        AND p.user.id IN (
            SELECT f.following.id FROM Follow f WHERE f.follower.id = :userId
        )
        ORDER BY p.createdAt DESC
        """)
    Page<Prompt> findFollowingFeed(@Param("userId") Long userId, Pageable pageable);

    // Trending - Reddit/HN style time-decay ranking
    @Query("""
        SELECT p FROM Prompt p 
        WHERE p.deleted = false 
        ORDER BY (p.likeCount * 2 + p.saveCount * 3) / 
                 POWER(EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - p.createdAt)) / 3600 + 2, 1.5) DESC
        """)
    Page<Prompt> findTrending(Pageable pageable);

    // User profile posts
    Page<Prompt> findByUserIdAndDeletedFalseOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // Full text search - basic keyword search in title and prompt body
    @Query("""
        SELECT DISTINCT p FROM Prompt p 
        WHERE p.deleted = false 
        AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) 
             OR LOWER(p.promptBody) LIKE LOWER(CONCAT('%', :keyword, '%')))
        ORDER BY p.createdAt DESC
        """)
    Page<Prompt> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // Search with difficulty filter
    @Query("""
        SELECT DISTINCT p FROM Prompt p 
        WHERE p.deleted = false 
        AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) 
             OR LOWER(p.promptBody) LIKE LOWER(CONCAT('%', :keyword, '%')))
        AND (:difficulty IS NULL OR p.difficulty = :difficulty)
        AND (:platform IS NULL OR LOWER(p.platform) = LOWER(:platform))
        AND (:verifiedOnly = false OR p.aiVerified = true)
        ORDER BY p.createdAt DESC
        """)
    Page<Prompt> searchWithFilters(
        @Param("keyword") String keyword,
        @Param("difficulty") com.sureprompt.entity.Difficulty difficulty,
        @Param("platform") String platform,
        @Param("verifiedOnly") boolean verifiedOnly,
        Pageable pageable
    );

    List<Prompt> findTop5ByUserIdAndDeletedFalseOrderByLikeCountDesc(Long userId);

    long countByUserIdAndDeletedFalse(Long userId);

    @Query("SELECT COALESCE(AVG(p.aiScore), 0) FROM Prompt p WHERE p.user.id = :userId AND p.deleted = false AND p.aiScore IS NOT NULL")
    Double findAverageAiScoreByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(p) > 0 FROM Prompt p WHERE p.user.id = :userId")
    boolean existsByUserId(@Param("userId") Long userId);

    long countByDeletedFalse();

    @Query("SELECT COUNT(p) FROM Prompt p WHERE p.user.id = :userId AND p.deleted = false AND p.createdAt >= :since")
    long countRecentPromptsByUserId(@Param("userId") Long userId, @Param("since") java.time.LocalDateTime since);
}
