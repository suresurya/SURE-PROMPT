package com.sureprompt.repository;

import com.sureprompt.entity.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {

    List<Collection> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT c FROM Collection c WHERE c.isPublic = true ORDER BY c.createdAt DESC")
    List<Collection> findPublicCollections();

    @Query("SELECT c FROM Collection c WHERE c.user.id = :userId AND c.isPublic = true")
    List<Collection> findPublicByUserId(@Param("userId") Long userId);
}
