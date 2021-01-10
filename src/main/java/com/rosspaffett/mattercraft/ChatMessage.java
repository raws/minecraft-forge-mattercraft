package com.rosspaffett.mattercraft;

public class ChatMessage {
    private final String body;
    private final String username;

    ChatMessage(String username, String body) {
        this.body = body;
        this.username = username;
    }

    public String getBody() {
        return this.body;
    }

    public String getUsername() {
        return this.username;
    }
}
