package com.sureprompt.service;

import com.sureprompt.entity.Report;
import com.sureprompt.entity.ReportStatus;
import com.sureprompt.entity.TargetType;
import com.sureprompt.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    
    // Simple in-memory rate limiter: userId -> lastReportTimestamp
    private final Map<Long, Long> rateLimitMap = new ConcurrentHashMap<>();
    private static final long RATE_LIMIT_MS = 60_000; // 1 minute
    private static final int MAX_REPORTS_PER_WINDOW = 5;

    @Transactional
    public void createReport(Long reporterId, TargetType targetType, Long targetId, String reason) {
        // 1. Rate Limit Check
        long now = System.currentTimeMillis();
        Long lastTime = rateLimitMap.get(reporterId);
        
        if (lastTime != null && (now - lastTime) < (RATE_LIMIT_MS / MAX_REPORTS_PER_WINDOW)) {
            throw new RuntimeException("You are reporting too frequently. Please wait a moment.");
        }
        rateLimitMap.put(reporterId, now);

        // 2. Duplicate Check (Handled by DB Unique Index idx_uq_reports_unique, but catching early)
        if (reportRepository.existsByReporterIdAndTargetTypeAndTargetIdAndStatus(
                reporterId, targetType, targetId, ReportStatus.PENDING)) {
            throw new RuntimeException("You have already reported this item.");
        }

        // 3. Create Report
        Report report = Report.builder()
                .reporterId(reporterId)
                .targetType(targetType)
                .targetId(targetId)
                .reason(reason)
                .status(ReportStatus.PENDING)
                .build();
        
        reportRepository.save(report);
    }
}
