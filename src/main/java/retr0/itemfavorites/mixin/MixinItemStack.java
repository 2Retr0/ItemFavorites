package retr0.itemfavorites.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import retr0.itemfavorites.extension.ExtensionItemStack;

@Mixin(ItemStack.class)
public abstract class MixinItemStack implements ExtensionItemStack {
    @Shadow private boolean empty;

    @Unique private static final String FAVORITE_KEY = "Favorite";
    @Unique private boolean isFavorite = false;

    @Override
    public void setFavorite(boolean status) {
        isFavorite = status;
    }

    @Override
    public boolean isFavorite() {
        return !empty && isFavorite;
    }



    /**
     * Ensures that the favorite status from nbt is copied into the created item stack.
     */
    @Inject(method = "<init>(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("TAIL"))
    private void initializeFavoriteStatus(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("Favorite")) setFavorite(nbt.getBoolean(FAVORITE_KEY));
    }



    /**
     * Suppresses favorite status from being inherited into copies if the stack is not entirely removed.
     */
    @Inject(method = "split", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void modifyCopyFavoriteStatus(
        int amount, CallbackInfoReturnable<ExtensionItemStack> cir, int countDecrement)
    {
        // For any splits where the stack is not completely transferred to the copy, we ensure the copy is not a favorite.
        if (empty && countDecrement > 0) return;

        var copiedStack = cir.getReturnValue();
        copiedStack.setFavorite(false);

        cir.setReturnValue(copiedStack);
    }



    /**
     * Ensures inclusion of a favorite key in the returned nbt.
     */
    @Inject(method = "writeNbt", at = @At("RETURN"), cancellable = true)
    private void writeFavoriteStatus(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
        var copiedNbt = cir.getReturnValue();
        copiedNbt.putBoolean(FAVORITE_KEY, isFavorite());

        cir.setReturnValue(copiedNbt);
    }



    /**
     * Ensures that a copy of a favorite item stack is also a copy.
     */
    @Inject(method = "copy", at = @At("RETURN"), cancellable = true)
    private void copyFavoriteStatus(CallbackInfoReturnable<ExtensionItemStack> cir) {
        var itemStack = cir.getReturnValue();
        itemStack.setFavorite(isFavorite());

        cir.setReturnValue(itemStack);
    }
}
