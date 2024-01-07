package retr0.itemfavorites.network;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import retr0.itemfavorites.util.ModUsageManager;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static retr0.itemfavorites.ItemFavorites.MOD_ID;

public class ModUsageS2CPacket {
    public static final Identifier NOTIFY_MOD_USAGE_ID = new Identifier(MOD_ID, "notify_mod_usage");

    public static void send(PacketSender sender) {
        sender.sendPacket(NOTIFY_MOD_USAGE_ID, PacketByteBufs.empty());
//        ServerPlayNetworking.send(player, NOTIFY_MOD_USAGE_ID, PacketByteBufs.empty());
    }

    @Environment(EnvType.CLIENT)
    public static CompletableFuture<@Nullable PacketByteBuf> receive(
            MinecraftClient client, ClientLoginNetworkHandler handler, PacketByteBuf buf, Consumer<GenericFutureListener<? extends Future<? super Void>>> listenerAdder)
    {
        ModUsageManager.setServerModUsage(true);
//        client.execute(() -> ModUsageManager.setServerModUsage(true));

        return CompletableFuture.completedFuture(PacketByteBufs.empty());
    }
}
