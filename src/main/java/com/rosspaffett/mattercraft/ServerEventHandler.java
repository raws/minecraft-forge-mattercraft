package com.rosspaffett.mattercraft;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

public class ServerEventHandler {
    public static final String OUTGOING_MESSAGE_THREAD_NAME = "Mattercraft/OutgoingMessageThread";

    private ChatMessageSender outgoingMessageSender;

    @SubscribeEvent
    public void onServerChatEvent(ServerChatEvent event) {
        sendChatMessage(event.getUsername(), event.getMessage());
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        startSendingMessages();
    }

    @SubscribeEvent
    public void onServerStopping(FMLServerStoppingEvent event) {
        stopSendingMessages();
    }

    private void sendChatMessage(String username, String body) {
        ChatMessage message = new ChatMessage(username, body);
        this.outgoingMessageSender.enqueue(message);
    }

    private void startSendingMessages() {
        this.outgoingMessageSender = new ChatMessageSender(MattercraftConfig.baseUrl, MattercraftConfig.gateway,
            MattercraftConfig.apiToken);

        Thread outgoingMessageThread = new Thread(this.outgoingMessageSender, OUTGOING_MESSAGE_THREAD_NAME);
        outgoingMessageThread.start();
    }

    private void stopSendingMessages() {
        this.outgoingMessageSender.stop();
    }
}
