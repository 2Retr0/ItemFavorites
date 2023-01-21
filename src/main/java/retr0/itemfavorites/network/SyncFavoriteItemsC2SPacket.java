package retr0.itemfavorites.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import retr0.itemfavorites.ItemFavorites;
import retr0.itemfavorites.extension.ExtensionItemStack;

import static retr0.itemfavorites.ItemFavorites.MOD_ID;

public record SyncFavoriteItemsC2SPacket(int syncId, int slotId, boolean favoriteStatus) {
    public static final Identifier SYNC_FAVORITE_ITEMS_ID = new Identifier(MOD_ID, "sync_favorite_items");

    public static void send(SyncFavoriteItemsC2SPacket syncData) {
        var buf = PacketByteBufs.create();

        buf.writeInt(syncData.syncId);
        buf.writeInt(syncData.slotId);
        buf.writeBoolean(syncData.favoriteStatus);

        ClientPlayNetworking.send(SYNC_FAVORITE_ITEMS_ID, buf);
    }



    /**
     * Executes a quick stack operation for the sender player.
     */
    public static void receive(
        MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf,
        PacketSender responseSender)
    {
        var syncId = buf.readInt();
        var slotId = buf.readInt();
        var favoriteStatus = buf.readBoolean();

        server.execute(() -> {
            ItemFavorites.LOGGER.info("Recieved SyncFavoriteItemsC2SPacket!");
            var screenHandler = player.currentScreenHandler;
            if (syncId != screenHandler.syncId) {
               ItemFavorites.LOGGER.warn("Ignoring favorite status update in mismatching container. Click in {}, " +
                       "player has {}.", syncId, screenHandler.syncId);
               return;
            }

            var itemStack = slotId >= 0 ? screenHandler.slots.get(slotId).getStack() : screenHandler.getCursorStack();
            if (!itemStack.isEmpty())
                ((ExtensionItemStack) (Object) itemStack).setFavorite(favoriteStatus);
        });
    }
}
