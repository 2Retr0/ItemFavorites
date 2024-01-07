package retr0.itemfavorites.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import retr0.itemfavorites.ItemFavorites;
import retr0.itemfavorites.extension.ExtensionItemStack;

import static retr0.itemfavorites.ItemFavorites.MOD_ID;

public class SyncFavoriteItemsC2SPacket {
    public static final Identifier SYNC_FAVORITE_ITEMS_ID = new Identifier(MOD_ID, "sync_favorite_items");

    /**
     * Sends a {@link SyncFavoriteItemsC2SPacket} to sync the favorite status of an {@link ItemStack} with the server.
     * @param syncId The client player's current {@link ScreenHandler} {@code syncId}.
     * @param slotId The {@code slotId} for the slot to be synced.
     * @param favoriteStatus The altered favorite status of the {@link ItemStack} associated with the slot.
     */
    @Environment(EnvType.CLIENT)
    public static void send(int syncId, int slotId, boolean favoriteStatus) {
        var buf = PacketByteBufs.create();

        buf.writeInt(syncId);
        buf.writeInt(slotId);
        buf.writeBoolean(favoriteStatus);

        ClientPlayNetworking.send(SYNC_FAVORITE_ITEMS_ID, buf);
    }

    /**
     * Sets the favorite status of the item stack held in the slot with the {@code slotId} from the packet.
     */
    public static void receive(
        MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf,
        PacketSender responseSender)
    {
        var syncId = buf.readInt();
        var slotId = buf.readInt();
        var favoriteStatus = buf.readBoolean();

        server.execute(() -> {
            var screenHandler = player.currentScreenHandler;
            if (syncId != screenHandler.syncId) {
               ItemFavorites.LOGGER.warn("Ignoring favorite status update in mismatching container. Click in {}, " +
                       "player has {}.", syncId, screenHandler.syncId);
               return;
            }

            var itemStack = (slotId >= 0 && slotId < screenHandler.slots.size()) ?
                screenHandler.slots.get(slotId).getStack() : screenHandler.getCursorStack();
            if (itemStack.isEmpty()) return;

            ((ExtensionItemStack) (Object) itemStack).setFavorite(favoriteStatus);
            screenHandler.syncState();
        });
    }
}
