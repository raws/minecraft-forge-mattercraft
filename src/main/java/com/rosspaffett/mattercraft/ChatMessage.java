package com.rosspaffett.mattercraft;

import net.minecraft.util.text.StringTextComponent;

public class ChatMessage {
    private String protocol;
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

    public String humanizedProtocol() {
        return MatterbridgeProtocol.humanize(protocol);
    }

    public String textWithUsername() {
        return "<" + getUsername() + "> " + getText();
    }

    public String toString() {
        if (protocol == null) {
            return toStringWithoutProtocol();
        } else {
            return toStringWithProtocol();
        }
    }

    private String toStringWithProtocol() {
        return "[" + humanizedProtocol() + "] " + textWithUsername();
    }

    private String toStringWithoutProtocol() {
        return textWithUsername();
    }

    public StringTextComponent toTextComponent() {
        return new StringTextComponent(toString());
    }
}
