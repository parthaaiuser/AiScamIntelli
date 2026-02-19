package com.honeypot.dto;

import lombok.Data;
import java.util.List;

@Data
public class IncomingRequest {
    private String sessionId;
    private Message message;
    private List<Message> conversationHistory;
    private Metadata metadata;

    @Data
    public static class Message {
        private String sender;
        private String text;
        private String timestamp;
    }

    @Data
    public static class Metadata {
        private String channel;
        private String language;
        private String locale;
    }
}