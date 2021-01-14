package com.rosspaffett.mattercraft;

import com.rosspaffett.mattercraft.matterbridge.MatterbridgeApiClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChatMessageReceiver implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final long MILLISECONDS_BETWEEN_REQUESTS = 1000;

    private final MatterbridgeApiClient matterbridgeApiClient;
    private final ConcurrentLinkedQueue<ChatMessage> messageQueue;
    private volatile boolean shouldStop = false;

    ChatMessageReceiver(String baseUrl, String gateway, String apiToken) {
        this.matterbridgeApiClient = new MatterbridgeApiClient(baseUrl, gateway, apiToken);
        this.messageQueue = new ConcurrentLinkedQueue<>();
    }

    public ChatMessage poll() {
        return messageQueue.poll();
    }

    public void run() {
        ChatMessage[] messages;

        while (shouldContinueRunning()) {
            try {
                messages = matterbridgeApiClient.getUnreadChatMessages();

                for (ChatMessage message : messages) {
                    messageQueue.add(message);
                }

                Thread.sleep(MILLISECONDS_BETWEEN_REQUESTS);
            } catch (IOException e) {
                LOGGER.error("Error connecting to Matterbridge API: " + e.getMessage());
            } catch (MatterbridgeApiClient.MatterbridgeApiErrorException e) {
                LOGGER.error(e.toString());
            } catch (InterruptedException e) {
                // Do nothing
            }
        }
    }

    private boolean shouldContinueRunning() {
        return !this.shouldStop;
    }

    public void stop() {
        this.shouldStop = true;
    }
}
