package com.rosspaffett.mattercraft;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChatMessageReceiver implements Runnable {
    private static final Gson GSON = new Gson();
    private static final Logger LOGGER = LogManager.getLogger();

    private final MatterbridgeApiClient matterbridge;
    private final ConcurrentLinkedQueue<ChatMessage> messages;
    private volatile boolean shouldStop = false;

    ChatMessageReceiver(String baseUrl, String gateway, String apiToken) {
        this.matterbridge = new MatterbridgeApiClient(baseUrl, gateway, apiToken);
        this.messages = new ConcurrentLinkedQueue<>();
    }

    private void parseChatMessage(String json) {
        ChatMessage message = GSON.fromJson(json, ChatMessage.class);

        if (message.getText() != null
            && message.getUsername() != null
            && message.getText().length() > 0
            && message.getUsername().length() > 0) {
            messages.add(message);
        } else {
            LOGGER.debug("Discarding message: {}", json);
        }
    }

    public ChatMessage poll() {
        return messages.poll();
    }

    public void run() {
        try {
            HttpURLConnection connection = matterbridge.streamMessagesConnection();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;

                while (shouldContinueRunning()) {
                    try {
                        line = reader.readLine();
                        if (line == null) break;
                        parseChatMessage(line);
                    } catch (SocketTimeoutException e) {
                        // Try reading again
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("Couldn't connect to Matterbridge API: {}", e.getMessage());
        }
    }

    private boolean shouldContinueRunning() {
        return !this.shouldStop;
    }

    public void stop() {
        this.shouldStop = true;
    }
}
