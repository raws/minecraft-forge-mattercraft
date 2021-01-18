package com.rosspaffett.mattercraft;

import net.minecraft.util.text.StringTextComponent;

public class ChatMessage {
    private final String text;
    private final String username;

    ChatMessage(String username, String text) {
        this.text = text;
        this.username = username;
    }

    public String getText() {
        return this.text;
    }

    public String getUsername() {
        return this.username;
    }

    public String toString() {
        return "<" + getUsername() + "> " + getText();
    }

    public StringTextComponent toTextComponent() {
        return new StringTextComponent(toString());
    }
}
