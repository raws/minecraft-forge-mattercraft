package com.rosspaffett.mattercraft;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerEventHandler {
    private static final String INCOMING_MESSAGE_THREAD_NAME = "Mattercraft/IncomingMessageThread";
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String OUTGOING_MESSAGE_THREAD_NAME = "Mattercraft/OutgoingMessageThread";

    private ChatMessageBroadcaster incomingMessageBroadcaster;
    private ChatMessageReceiver incomingMessageReceiver;
    private ChatMessageSender outgoingMessageSender;
    private MinecraftServer server;

    @SubscribeEvent
    public void onServerChatEvent(ServerChatEvent event) {
        sendOutgoingChatMessage(event.getUsername(), event.getMessage());
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        this.server = event.getServer();

        startReceivingMessages();
        startSendingMessages();

        LOGGER.info("Mattercraft is relaying chat to Matterbridge gateway \"{}\" at {}",
            MattercraftConfig.gateway, MattercraftConfig.baseUrl);
    }

    @SubscribeEvent
    public void onServerStopping(FMLServerStoppingEvent event) {
        stopReceivingMessages();
        stopSendingMessages();

        this.server = null;
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        sendIncomingChatMessage();
    }

    private void sendIncomingChatMessage() {
        ChatMessage message = incomingMessageReceiver.poll();
        if (message == null) { return; }
        incomingMessageBroadcaster.broadcast(message);
    }

    private void sendOutgoingChatMessage(String username, String body) {
        ChatMessage message = new ChatMessage(username, body);
        this.outgoingMessageSender.enqueue(message);
    }

    private void startIncomingMessageThread() {
        Thread incomingMessageThread = new Thread (this.incomingMessageReceiver, INCOMING_MESSAGE_THREAD_NAME);
        incomingMessageThread.start();
    }

    private void startOutgoingMessageThread() {
        Thread outgoingMessageThread = new Thread(this.outgoingMessageSender, OUTGOING_MESSAGE_THREAD_NAME);
        outgoingMessageThread.start();
    }

    private void startReceivingMessages() {
        this.incomingMessageBroadcaster = new ChatMessageBroadcaster(server);
        this.incomingMessageReceiver = new ChatMessageReceiver(MattercraftConfig.baseUrl, MattercraftConfig.gateway,
            MattercraftConfig.apiToken);

        startIncomingMessageThread();
    }

    private void startSendingMessages() {
        this.outgoingMessageSender = new ChatMessageSender(MattercraftConfig.baseUrl, MattercraftConfig.gateway,
            MattercraftConfig.apiToken);

        startOutgoingMessageThread();
    }

    private void stopReceivingMessages() {
        this.incomingMessageReceiver.stop();
    }

    private void stopSendingMessages() {
        this.outgoingMessageSender.stop();
    }
}
