package com.honeypot.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ApiResponse {
    private String status;
    private boolean scamDetected;
    private EngagementMetrics engagementMetrics;
    private ExtractedIntelligence extractedIntelligence;
    private String agentNotes;
    private String agentReply; 

    @Data
    @Builder
    public static class EngagementMetrics {
        private int engagementDurationSeconds;
        private int totalMessagesExchanged;
    }

    @Data
    @Builder
    public static class ExtractedIntelligence {
        private List<String> bankAccounts;
        private List<String> upiIds;
        private List<String> phishingLinks;
        private List<String> phoneNumbers;
        private List<String> suspiciousKeywords;
    }
}