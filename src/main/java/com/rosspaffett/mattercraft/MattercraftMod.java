package com.rosspaffett.mattercraft;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.commons.lang3.tuple.Pair;

@Mod(MattercraftMod.MOD_ID)
@Mod.EventBusSubscriber(value = Dist.DEDICATED_SERVER, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MattercraftMod {
    public static final String MOD_ID = "mattercraft";

    public MattercraftMod() {
        allowClientVersionMismatch();
        registerConfig();
        registerServerEventHandler();
    }

    /**
     * Tell Forge to ignore any Mattercraft version mismatch between the client and server, since the server
     * implementation is the only one that's used.
     *
     * This overrides the default display test extension point implemented in
     * {@link net.minecraftforge.fml.ModContainer#ModContainer(IModInfo)} according to the example provided by
     * {@link net.minecraftforge.fml.ExtensionPoint#DISPLAYTEST}.
     */
    private void allowClientVersionMismatch() {
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(
            () -> FMLNetworkConstants.IGNORESERVERONLY,
            (remoteVersion, isNetwork) -> true
        ));
    }

    @SubscribeEvent
    public static void onModConfigEvent(ModConfig.ModConfigEvent event) {
        if (event.getConfig().getSpec() == MattercraftConfig.SPEC) {
            MattercraftConfig.cacheValuesFromSpec();
        }
    }

    private void registerConfig() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, MattercraftConfig.SPEC);
    }

    private void registerServerEventHandler() {
        ServerEventHandler serverEventHandler = new ServerEventHandler();
        MinecraftForge.EVENT_BUS.register(serverEventHandler);
    }
}
