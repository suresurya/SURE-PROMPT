package com.sureprompt.repository;

import com.sureprompt.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);

    long countByUserIdAndReadFalse(Long userId);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.userId = :userId AND n.read = false")
    void markAllAsRead(@Param("userId") Long userId);
}
