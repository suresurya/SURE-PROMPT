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

    // Trending - last 7 days, ranked by likes + saves
    @Query("""
        SELECT p FROM Prompt p 
        WHERE p.deleted = false 
        AND p.createdAt >= :since
        ORDER BY (p.likeCount + p.saveCount) DESC
        """)
    Page<Prompt> findTrending(@Param("since") java.time.LocalDateTime since, Pageable pageable);

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
}
