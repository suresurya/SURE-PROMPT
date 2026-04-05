package com.sureprompt.service;

import com.sureprompt.entity.*;
import com.sureprompt.repository.PromptRepository;
import com.sureprompt.repository.ReportRepository;
import com.sureprompt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final PromptRepository promptRepository;
    private final com.sureprompt.repository.AdminActionRepository adminActionRepository;

    public Map<String, Object> getDashboardStats() {
        return Map.of(
            "totalUsers", userRepository.count(),
            "totalPrompts", promptRepository.countByDeletedFalse(),
            "pendingReports", reportRepository.countByStatus(ReportStatus.PENDING)
        );
    }

    public List<Report> getPendingReports() {
        return reportRepository.findAllByStatusOrderByCreatedAtDesc(ReportStatus.PENDING);
    }

    @Transactional
    public void resolveReport(Long reportId, String action, Long adminId, String note) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        if (report.getStatus() != ReportStatus.PENDING) {
            throw new RuntimeException("Report already processed");
        }

        // Record Audit Log
        adminActionRepository.save(com.sureprompt.entity.AdminAction.builder()
                .adminId(adminId)
                .action(action)
                .targetType(report.getTargetType().name())
                .targetId(report.getTargetId())
                .note(note)
                .build());

        switch (action) {
            case "BAN_USER":
                Long userId = report.getTargetId();
                User user = userRepository.findById(userId).orElseThrow();
                user.setBanned(true);
                userRepository.save(user);
                break;

            case "DELETE_PROMPT":
                Long promptId = report.getTargetId();
                Prompt prompt = promptRepository.findById(promptId).orElseThrow();
                prompt.setDeleted(true);
                promptRepository.save(prompt);
                break;

            case "DELETE_COMMENT":
                // Logic for comment soft-delete would go here 
                // (Assuming Comment entity has deleted field)
                break;
        }

        report.setAdminNote(note);
        report.setResolvedBy(adminId);
        report.setStatus(ReportStatus.RESOLVED);
        reportRepository.save(report);
    }

    @Transactional
    public void rejectReport(Long reportId, Long adminId, String note) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        if (report.getStatus() != ReportStatus.PENDING) {
            throw new RuntimeException("Report already processed");
        }

        // Record Audit Log
        adminActionRepository.save(com.sureprompt.entity.AdminAction.builder()
                .adminId(adminId)
                .action("REJECT_REPORT")
                .targetType(report.getTargetType().name())
                .targetId(report.getTargetId())
                .note(note)
                .build());

        report.setAdminNote(note);
        report.setResolvedBy(adminId);
        report.setStatus(ReportStatus.REJECTED);
        reportRepository.save(report);
    }

    @Transactional
    public void toggleUserBan(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setBanned(!user.isBanned());
        userRepository.save(user);
    }
}
