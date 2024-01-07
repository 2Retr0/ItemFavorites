package retr0.itemfavorites.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import retr0.itemfavorites.extension.ExtensionItemStack;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity extends PlayerEntity {
    /**
     * Suppresses dropping favorite items from the in-game hotbar.
     */
    @Inject(method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
    private void suppressFavoriteDrop(boolean entireStack, CallbackInfoReturnable<Boolean> cir) {
        var mainHandStack = this.getInventory().getMainHandStack();

        if (!ExtensionItemStack.isFavorite(mainHandStack)) return;

        cir.setReturnValue(false);
        cir.cancel();
    }

    public MixinServerPlayerEntity(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }
}
