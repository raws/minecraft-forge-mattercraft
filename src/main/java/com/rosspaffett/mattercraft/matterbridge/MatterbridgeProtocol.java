package com.rosspaffett.mattercraft.matterbridge;

import java.util.HashMap;

/**
 * Maps Matterbridge's chat protocol IDs to human-readable versions.
 */
public class MatterbridgeProtocol {
    private static final HashMap<String, String> IDS_TO_HUMANIZED_NAMES = new HashMap<>();

    static {
        IDS_TO_HUMANIZED_NAMES.put("discord", "Discord");
        IDS_TO_HUMANIZED_NAMES.put("irc", "IRC");
    }

    public static String humanize(String id) {
        return IDS_TO_HUMANIZED_NAMES.getOrDefault(id, id);
    }
}
