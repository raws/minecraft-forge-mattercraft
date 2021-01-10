package com.rosspaffett.mattercraft.matterbridge;

public class SendMessageRequestBody {
    public final String gateway;
    public final String text;
    public final String username;

    public SendMessageRequestBody(String gateway, String username, String text) {
        this.gateway = gateway;
        this.text = text;
        this.username = username;
    }
}
