package com.rosspaffett.mattercraft.matterbridge;

import com.google.gson.Gson;

public class SendMessageRequestBody {
    public static final Gson GSON = new Gson();

    public final String gateway;
    public final String text;
    public final String username;

    public SendMessageRequestBody(String gateway, String username, String text) {
        this.gateway = gateway;
        this.text = text;
        this.username = username;
    }

    public String toJson() {
        return GSON.toJson(this);
    }
}
