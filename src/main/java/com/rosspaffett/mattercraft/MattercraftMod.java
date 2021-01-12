package com.rosspaffett.mattercraft;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(MattercraftMod.MOD_ID)
@Mod.EventBusSubscriber(Dist.DEDICATED_SERVER)
public class MattercraftMod {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "mattercraft";

    private ChatMessageSender outgoingMessageSender;

    public MattercraftMod() {
        registerAsServerOnlyMod();
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

    /**
     * Tell Forge to ignore any Mattercraft version mismatch between the client and server, since the server
     * implementation is the only one that's used.
     *
     * This overrides the default display test extension point implemented in
     * {@link net.minecraftforge.fml.ModContainer#ModContainer(IModInfo)} according to the example provided by
     * {@link net.minecraftforge.fml.ExtensionPoint#DISPLAYTEST}.
     */
    private void registerAsServerOnlyMod() {
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(
            () -> FMLNetworkConstants.IGNORESERVERONLY,
            (remoteVersion, isNetwork) -> true
        ));
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
