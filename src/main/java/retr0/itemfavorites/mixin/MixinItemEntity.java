package retr0.itemfavorites.mixin;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import retr0.itemfavorites.extension.ExtensionItemStack;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntity {
    /**
     * Prevents dropped items from being a favorite item.
     */
    @ModifyVariable(
        method = "setStack",
        at = @At(
            target = "Lnet/minecraft/entity/ItemEntity;getDataTracker()Lnet/minecraft/entity/data/DataTracker;",
            value = "INVOKE"),
        argsOnly = true)
    private ItemStack removeFavoriteStatus(ItemStack stack) {
        ExtensionItemStack.setFavorite(stack, false);
        return stack;
    }
}
