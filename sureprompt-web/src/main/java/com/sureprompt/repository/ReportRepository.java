package com.sureprompt.repository;

import com.sureprompt.entity.Report;
import com.sureprompt.entity.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    
    List<Report> findAllByStatusOrderByCreatedAtDesc(ReportStatus status);
    
    long countByStatus(ReportStatus status);

    boolean existsByReporterIdAndTargetTypeAndTargetIdAndStatus(
            Long reporterId, com.sureprompt.entity.TargetType targetType, Long targetId, ReportStatus status);
}
