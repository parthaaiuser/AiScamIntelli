package com.honeypot.service;

import com.honeypot.dto.ApiResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class GuviCallbackService {

    private final String GUVI_ENDPOINT = "https://hackathon.guvi.in/api/updateHoneyPotFinalResult";
    private final RestTemplate restTemplate = new RestTemplate();

    public void sendFinalReport(String sessionId, ApiResponse response) {
        if (!response.isScamDetected()) return;

        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("sessionId", sessionId);
            payload.put("scamDetected", response.isScamDetected());
            payload.put("totalMessagesExchanged", response.getEngagementMetrics().getTotalMessagesExchanged());
            payload.put("extractedIntelligence", response.getExtractedIntelligence());
            payload.put("agentNotes", response.getAgentNotes());

            // Sending the POST request
            restTemplate.postForObject(GUVI_ENDPOINT, payload, String.class);
            System.out.println("✅ REPORT SENT TO GUVI: Session " + sessionId);

        } catch (Exception e) {
            System.err.println("❌ REPORT FAILED: " + e.getMessage());
        }
    }
}