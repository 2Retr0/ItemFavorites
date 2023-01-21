package retr0.itemfavorites.mixin;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import retr0.itemfavorites.extension.ExtensionItemStack;

@Mixin(Slot.class)
public abstract class MixinSlot {
    @Shadow @Final public Inventory inventory;

    /**
     * Ensures that any retrievals of an item from a non-{@link PlayerInventory} results in a non-favorite item
     * (favorite items should only exist in player inventories).
     */
    @Inject(method = "getStack", at = @At("RETURN"))
    private void checkInventoryType(CallbackInfoReturnable<ItemStack> cir) {
        if (!(inventory instanceof PlayerInventory))
            ((ExtensionItemStack) (Object) cir.getReturnValue()).setFavorite(false);
    }
}
