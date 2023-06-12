package retr0.itemfavorites.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import retr0.itemfavorites.extension.ExtensionItemStack;

@Mixin(ScreenHandler.class)
public abstract class MixinScreenHandler {
    @Shadow private ItemStack cursorStack;
    @Unique private Slot quickCraftSlot;

    /**
     * Caches the current quick craft slot being considered for use in
     * {@link MixinScreenHandler#suppressFavoriteStatus(ItemStack)}
     */
    /* [  8]    [  2]                             int  k                                                   -         */
    /* [  9]    [  0]                        Iterator  var9                                                -         */
    /* [ 10]    [  0]                            Slot  slot2                                             >>YES<<     */
    @ModifyVariable(
        method = "internalOnSlotClick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/screen/slot/Slot;getMaxItemCount(Lnet/minecraft/item/ItemStack;)I",
            shift = At.Shift.BEFORE,
            ordinal = 0),
        ordinal = 0)
    private Slot cacheQuickCraftSlot(Slot slot) {
        quickCraftSlot = slot;

        return slot;
    }



    /**
     * Prevents all resulting copied item stacks from a {@link SlotActionType#QUICK_CRAFT} from being favorites unless
     * the stack they're combined with is already a favorite.
     */
    @ModifyArg(
        method = "internalOnSlotClick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/screen/slot/Slot;getMaxItemCount(Lnet/minecraft/item/ItemStack;)I",
            ordinal = 0),
        index = 0)
    private ItemStack suppressFavoriteStatus(ItemStack itemStack) {
        var targetSlotFavoriteStatus = ExtensionItemStack.isFavorite(quickCraftSlot.getStack());

        ExtensionItemStack.setFavorite(itemStack, targetSlotFavoriteStatus);

        return itemStack;
    }



    /**
     * Ensures favorite items during a {@link SlotActionType#PICKUP_ALL} action.
     */
    /* [ 11]    [  5]                             int  p                                                   -         */
    /* [ 12]    [  1]                            Slot  slot4                                               -         */
    /* [ 13]    [  1]                       ItemStack  itemStack6                                        >>YES<<     */
    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(
        method = "internalOnSlotClick",
        at = @At(
            value = "INVOKE_ASSIGN",
            target = "Lnet/minecraft/screen/slot/Slot;getStack()Lnet/minecraft/item/ItemStack;"),
        index = 13)
    private ItemStack skipFavoriteItems(ItemStack itemStack) {
        return ExtensionItemStack.isFavorite(itemStack) ? ItemStack.EMPTY : itemStack;
    }
}
