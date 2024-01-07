package retr0.itemfavorites.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import retr0.itemfavorites.ItemFavorites;
import retr0.itemfavorites.network.ModUsageS2CPacket;

public class ModUsageManager {
    private static boolean isInitialized = false;
    private static boolean doesServerUseMod = false;

    public static void init() {
        if (isInitialized) return;

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> doesServerUseMod = false);
            ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
                if (!doesServerUseMod())
                    ItemFavorites.LOGGER.warn("Mod is not present server-side! Item favoriting will be disabled!");
            });
        }

        // Whenever a client joins the server, notify them that the mod is installed--clients which have been notified
        // and have the mod installed will request to track inhabited time.
        ServerLoginConnectionEvents.QUERY_START.register((handler, server, sender, synchronizer) -> ModUsageS2CPacket.send(sender));
        isInitialized = true;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean doesServerUseMod() {
        return doesServerUseMod;
    }

    public static void setServerModUsage(boolean doesServerUseMod) {
        ModUsageManager.doesServerUseMod = doesServerUseMod;
    }

    private ModUsageManager() { }
}
