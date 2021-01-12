package com.rosspaffett.mattercraft;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class MattercraftConfig {
    public static final MatterbridgeConfig MATTERBRIDGE;
    public static final ForgeConfigSpec SPEC;

    static {
        final Pair<MatterbridgeConfig, ForgeConfigSpec> configSpecPair = new ForgeConfigSpec.Builder().
            configure(MatterbridgeConfig::new);
        MATTERBRIDGE = configSpecPair.getLeft();
        SPEC = configSpecPair.getRight();
    }

    public static String apiToken;
    public static String baseUrl;
    public static String gateway;

    /**
     * Cache the {@link net.minecraftforge.common.ForgeConfigSpec.ConfigValue}s from {@link MatterbridgeConfig} into
     * static class members using plain Java primitives, so that we don't need to repeatedly call
     * {@link ForgeConfigSpec.ConfigValue#get()}, which is relatively expensive.
     */
    protected static void cacheValuesFromSpec() {
        apiToken = MATTERBRIDGE.apiToken.get();
        baseUrl = MATTERBRIDGE.baseUrl.get();
        gateway = MATTERBRIDGE.gateway.get();
    }

    public static class MatterbridgeConfig {
        public final ForgeConfigSpec.ConfigValue<String> apiToken;
        public final ForgeConfigSpec.ConfigValue<String> baseUrl;
        public final ForgeConfigSpec.ConfigValue<String> gateway;

        public MatterbridgeConfig(ForgeConfigSpec.Builder builder) {
            builder.push("matterbridge");

            apiToken = builder
                .comment("Your Matterbridge API token")
                .define("api_token", "example-api-token");

            baseUrl = builder
                .comment("Matterbridge API base URL, including protocol")
                .define("base_url", "https://matterbridge.example.com");

            gateway = builder
                .comment("Matterbridge gateway name")
                .define("gateway", "example-gateway");

            builder.pop();
        }
    }
}
