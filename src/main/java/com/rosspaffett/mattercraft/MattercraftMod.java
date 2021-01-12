package com.rosspaffett.mattercraft;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(MattercraftMod.MOD_ID)
@Mod.EventBusSubscriber(Dist.DEDICATED_SERVER)
public class MattercraftMod {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "mattercraft";

    private ChatMessageSender outgoingMessageSender;

    public MattercraftMod() {
        MinecraftForge.EVENT_BUS.register(this);
    }

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
        this.outgoingMessageSender = new ChatMessageSender("example-base-url", "example-gateway",
            "example-api-token");

        Thread outgoingMessageThread = new Thread(this.outgoingMessageSender, "OutgoingMessageThread");
        outgoingMessageThread.start();
    }

    private void stopSendingMessages() {
        this.outgoingMessageSender.stop();
    }
}
