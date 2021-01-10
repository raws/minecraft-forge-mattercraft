package com.rosspaffett.mattercraft;

import com.google.gson.Gson;
import com.rosspaffett.mattercraft.matterbridge.SendMessageRequestBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ChatMessageSender implements Runnable {
    private static final Gson GSON = new Gson();
    private static final Logger LOGGER = LogManager.getLogger();

    private final ConcurrentLinkedQueue<ChatMessage> messageQueue;
    private volatile boolean shouldStop = false;

    ChatMessageSender() {
        this.messageQueue = new ConcurrentLinkedQueue<ChatMessage>();
    }

    public void enqueue(ChatMessage message) {
        this.messageQueue.add(message);
    }

    public void run() {
        ChatMessage message;

        while (shouldContinueRunning()) {
            message = messageQueue.poll();

            if (message != null) {
                sendMessageToMatterbridge(message);
            }
        }
    }

    private void sendMessageToMatterbridge(ChatMessage message) {
        SendMessageRequestBody requestBody = new SendMessageRequestBody("test", message.getUsername(),
            message.getBody());

        LOGGER.info("Sending message to Matterbridge: " + GSON.toJson(requestBody));
    }

    private boolean shouldContinueRunning() {
        return !this.shouldStop;
    }

    public void stop() {
        this.shouldStop = true;
    }
}
