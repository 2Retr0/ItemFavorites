package retr0.itemfavorites.mixin;

import io.netty.buffer.ByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import retr0.itemfavorites.extension.ExtensionItemStack;
import retr0.itemfavorites.util.ModUsageManager;

@Mixin(PacketByteBuf.class)
public abstract class MixinPacketByteBuf {
    @Shadow public abstract ByteBuf writeBoolean(boolean value);
    @Shadow public abstract boolean readBoolean();

    @Unique private static boolean isClient = false;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void getEnvironment(ByteBuf parent, CallbackInfo ci) {
        isClient = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    /**
     * Adds the favorite status of the {@link ItemStack} to the packet.
     */
    @Inject(method = "writeItemStack", at = @At("RETURN"))
    private void writeFavoriteStatus(ItemStack stack, CallbackInfoReturnable<PacketByteBuf> cir) {
        if (isClient && !ModUsageManager.doesServerUseMod()) return;

        this.writeBoolean(((ExtensionItemStack) (Object) stack).isFavorite());
    }

    

    /**
     * Ensures the newly created {@link ItemStack} has the favorite status from the packet.
     */
    @Inject(method = "readItemStack", at = @At("RETURN"), cancellable = true)
    private void readFavoriteStatus(CallbackInfoReturnable<ExtensionItemStack> cir) {
        if (isClient && !ModUsageManager.doesServerUseMod()) return;

        var itemStack = cir.getReturnValue();
        itemStack.setFavorite(this.readBoolean());

        cir.setReturnValue(itemStack);
    }
}
