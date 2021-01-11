package com.rosspaffett.mattercraft;

import com.google.gson.Gson;
import com.rosspaffett.mattercraft.matterbridge.MatterbridgeApiClient;
import com.rosspaffett.mattercraft.matterbridge.SendMessageRequestBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChatMessageSender implements Runnable {
    private static final Gson GSON = new Gson();
    private static final Logger LOGGER = LogManager.getLogger();

    private final MatterbridgeApiClient matterbridgeApiClient;
    private final ConcurrentLinkedQueue<ChatMessage> messageQueue;
    private volatile boolean shouldStop = false;

    ChatMessageSender(String baseUrl, String gateway, String apiToken) {
        this.matterbridgeApiClient = new MatterbridgeApiClient(baseUrl, gateway, apiToken);
        this.messageQueue = new ConcurrentLinkedQueue<ChatMessage>();
    }

    public void enqueue(ChatMessage message) {
        this.messageQueue.add(message);
    }

    public void run() {
        ChatMessage message;

        while (shouldContinueRunning()) {
            message = this.messageQueue.poll();

            if (message != null) {
                sendMessageToMatterbridge(message);
            }
        }
    }

    private void sendMessageToMatterbridge(ChatMessage message) {
        try {
            this.matterbridgeApiClient.sendChatMessage(message);
        } catch (IOException e) {
            LOGGER.error("Error connecting to Matterbridge API: " + e.getMessage());
        }
    }

    private boolean shouldContinueRunning() {
        return !this.shouldStop;
    }

    public void stop() {
        this.shouldStop = true;
    }
}
