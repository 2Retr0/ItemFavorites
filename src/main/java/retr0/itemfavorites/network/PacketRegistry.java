package retr0.itemfavorites.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import static retr0.itemfavorites.network.SyncFavoriteItemsC2SPacket.SYNC_FAVORITE_ITEMS_ID;

public class PacketRegistry {
    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(SYNC_FAVORITE_ITEMS_ID, SyncFavoriteItemsC2SPacket::receive);
    }

    public static void registerS2CPackets() { }
}
