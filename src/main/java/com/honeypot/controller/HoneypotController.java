package com.honeypot.controller;

import com.honeypot.dto.ApiResponse;
import com.honeypot.dto.IncomingRequest;
import com.honeypot.service.AgentService;
import com.honeypot.service.GuviCallbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class HoneypotController {

    @Autowired
    private AgentService agentService;

    @Autowired
    private GuviCallbackService guviCallbackService;

    // YOUR SECRET KEY
    private final String MY_SECRET_KEY = "my-secret-hackathon-key";

    @PostMapping("/honeypot/message")
    public ResponseEntity<ApiResponse> handleMessage(
            @RequestHeader(value = "x-api-key", required = false) String apiKey,
            @RequestBody IncomingRequest request) {

        // 1. CHECK API KEY
        if (apiKey == null || !apiKey.equals(MY_SECRET_KEY)) {
            return ResponseEntity.status(401).build();
        }

        // 2. PROCESS THE MESSAGE
        ApiResponse response = agentService.processRequest(request);

        // 3. CHECK IF WE NEED TO REPORT TO GUVI
        // Logic: If we found a UPI ID, report it immediately!
        if (!response.getExtractedIntelligence().getUpiIds().isEmpty()) {
            guviCallbackService.sendFinalReport(request.getSessionId(), response);
        }

        // 4. RETURN ANSWER
        return ResponseEntity.ok(response);
    }
}