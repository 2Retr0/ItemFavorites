package retr0.itemfavorites.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import static retr0.itemfavorites.network.ModUsageS2CPacket.NOTIFY_MOD_USAGE_ID;
import static retr0.itemfavorites.network.SyncFavoriteItemsC2SPacket.SYNC_FAVORITE_ITEMS_ID;

public class PacketRegistry {
    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(SYNC_FAVORITE_ITEMS_ID, SyncFavoriteItemsC2SPacket::receive);
        ServerLoginNetworking.registerGlobalReceiver(NOTIFY_MOD_USAGE_ID, (server, handler, understood, buf, synchronizer, sender) -> {});
    }

    @Environment(EnvType.CLIENT)
    public static void registerS2CPackets() {
//        ClientPlayNetworking.registerGlobalReceiver(NOTIFY_MOD_USAGE_ID, ModUsageS2CPacket::receive);
        ClientLoginNetworking.registerGlobalReceiver(NOTIFY_MOD_USAGE_ID, ModUsageS2CPacket::receive);
    }
}
