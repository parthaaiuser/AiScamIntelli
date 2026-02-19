package com.honeypot.service;

import com.honeypot.dto.ApiResponse;
import com.honeypot.dto.IncomingRequest;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AgentService {

    // Regex to find UPI IDs (e.g., name@okicici)
    private final Pattern UPI_PATTERN = Pattern.compile("[a-zA-Z0-9.\\-_]{2,256}@[a-zA-Z]{2,64}");
    
    // Regex to find Indian Phone Numbers
    private final Pattern PHONE_PATTERN = Pattern.compile("(\\+91)?[6-9][0-9]{9}");

    public ApiResponse processRequest(IncomingRequest request) {
        String incomingText = request.getMessage().getText();
        
        // 1. DETECT SCAM (Simple keyword check)
        boolean isScam = detectScam(incomingText);

        // 2. EXTRACT INTELLIGENCE
        ApiResponse.ExtractedIntelligence intel = extractIntelligence(incomingText);

        // 3. GENERATE AGENT REPLY (Grandma Persona)
        String agentReply = generateAgentReply(isScam, incomingText);

        // 4. CALCULATE METRICS
        int msgCount = (request.getConversationHistory() != null) ? request.getConversationHistory().size() + 1 : 1;

        return ApiResponse.builder()
                .status("success")
                .scamDetected(isScam)
                .engagementMetrics(ApiResponse.EngagementMetrics.builder()
                        .totalMessagesExchanged(msgCount)
                        .engagementDurationSeconds(msgCount * 20)
                        .build())
                .extractedIntelligence(intel)
                .agentNotes(isScam ? "Scammer is using urgency tactics." : "Normal conversation.")
                .agentReply(agentReply)
                .build();
    }

    private boolean detectScam(String text) {
        String lower = text.toLowerCase();
        return lower.contains("block") || lower.contains("verify") || 
               lower.contains("urgent") || lower.contains("won") || 
               lower.contains("pay") || lower.contains("kyc");
    }

    private ApiResponse.ExtractedIntelligence extractIntelligence(String text) {
        List<String> upis = new ArrayList<>();
        Matcher upiMatcher = UPI_PATTERN.matcher(text);
        while (upiMatcher.find()) upis.add(upiMatcher.group());

        List<String> phones = new ArrayList<>();
        Matcher phoneMatcher = PHONE_PATTERN.matcher(text);
        while (phoneMatcher.find()) phones.add(phoneMatcher.group());

        return ApiResponse.ExtractedIntelligence.builder()
                .upiIds(upis)
                .phoneNumbers(phones)
                .bankAccounts(new ArrayList<>())
                .phishingLinks(new ArrayList<>())
                .suspiciousKeywords(new ArrayList<>())
                .build();
    }

    private String generateAgentReply(boolean isScam, String text) {
        if (!isScam) return "I am not interested.";
        
        // THE "GRANDMA" PERSONA
        String lower = text.toLowerCase();
        if (lower.contains("upi") || lower.contains("pay") || lower.contains("send")) {
            return "I am trying to send it! My grandson installed this app. Which button is for 'Pay'? I am so confused.";
        }
        if (lower.contains("block") || lower.contains("police")) {
            return "Oh my goodness! Police? I am an 80-year-old retired teacher. Please don't arrest me! Tell me what to do.";
        }
        return "I am scared. Why is this happening to me? Can I pay you in cash at the bank?";
    }
}