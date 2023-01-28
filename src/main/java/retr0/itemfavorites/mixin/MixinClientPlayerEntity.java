package retr0.itemfavorites.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import retr0.itemfavorites.extension.ExtensionInGameHud;
import retr0.itemfavorites.extension.ExtensionItemStack;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity extends PlayerEntity {
    /**
     * Suppresses dropping favorite items from the in-game hotbar. If a suppression does occur, triggers the bookmark
     * icon to render as a reminder that the item is a favorite.
     */
    @Inject(method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
    private void suppressFavoriteDrop(boolean entireStack, CallbackInfoReturnable<Boolean> cir) {
        var mainHandStack = this.getInventory().getMainHandStack();
        var inGameHud = MinecraftClient.getInstance().inGameHud;

        if (!ExtensionItemStack.isFavorite(mainHandStack)) return;

        ExtensionInGameHud.setBookmarkFade(inGameHud, 1000);
        cir.setReturnValue(false);
        cir.cancel();
    }

    public MixinClientPlayerEntity(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }
}
