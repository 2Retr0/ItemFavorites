package retr0.itemfavorites.compat.inventorysorter.mixin;

import net.kyrptonaught.inventorysorter.InventoryHelper;
import net.kyrptonaught.inventorysorter.SortCases;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import retr0.itemfavorites.extension.ExtensionItemStack;

import java.util.ArrayList;
import java.util.List;

@Pseudo @Mixin(InventoryHelper.class)
public class MixinInventoryHelper {
    @Unique private static ItemStack[] mergedStacks;
    @Unique private static boolean allowFavoriteCheck = false;

    @Inject(
        method = "sortInv(Lnet/minecraft/inventory/Inventory;IILnet/kyrptonaught/inventorysorter/SortCases$SortType;)V",
        at = @At("HEAD"))
    private static void createIndexCache(
        Inventory inv, int startSlot, int invSize, SortCases.SortType sortType, CallbackInfo ci)
    {
        mergedStacks = new ItemStack[invSize];
    }



    /**
     * Adds favorite items to {@code mergedStacks} at their respective index (or {@link ItemStack#EMPTY} if the item is
     * not a favorite).
     */
    @Inject(
        method = "sortInv(Lnet/minecraft/inventory/Inventory;IILnet/kyrptonaught/inventorysorter/SortCases$SortType;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/kyrptonaught/inventorysorter/InventoryHelper;addStackWithMerge(Ljava/util/List;Lnet/minecraft/item/ItemStack;)V"),
    locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void cacheFavoriteItemIndices(
        Inventory inv, int startSlot, int invSize, SortCases.SortType sortType, CallbackInfo ci, List<ItemStack> stacks,
        int i)
    {
        var itemStack = inv.getStack(startSlot + i);
        // We ignore non-favorite items as we need to wait for them to be sorted + merged first before adding them to
        // the array.
        mergedStacks[i] = ((ExtensionItemStack) (Object) itemStack).isFavorite() ? itemStack : ItemStack.EMPTY;
        allowFavoriteCheck = true;
    }



    /**
     * Merges the sorted + merged non-favorite items with the cached favorite items (retaining their original indices).
     */
    @ModifyVariable(
        method = "sortInv(Lnet/minecraft/inventory/Inventory;IILnet/kyrptonaught/inventorysorter/SortCases$SortType;)V",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;size()I",
            ordinal = 0))
    private static List<ItemStack> mergeCachedIndices(List<ItemStack> stacks) {
        var nextFreeIndex = 0;
        for (ItemStack stack : stacks) {
            // Skip over indices with favorite items.
            while (!mergedStacks[nextFreeIndex].isEmpty()) nextFreeIndex++;

            mergedStacks[nextFreeIndex] = stack;
        }
        return new ArrayList<>(List.of(mergedStacks));
    }



    /**
     * Prevents favorite items from being added to {@code stacks} to be sorted.
     *
     * @implNote <i>Because we don't add favorite items to {@code stacks}, they will not be later copied to the
     * inventory (favorite items will be deleted from the inventory without our other cache + merge operations!).</i>
     */
    @Inject(method = "addStackWithMerge", at = @At("HEAD"), cancellable = true)
    private static void preventFavoriteStack(List<ItemStack> stacks, ItemStack newStack, CallbackInfo ci) {
        // allowFavoriteCheck should be set to true (otherwise, the above Mixin has probably failed in which we don't
        // want to accidentally delete any items).
        if (((ExtensionItemStack) (Object) newStack).isFavorite() && allowFavoriteCheck)
            ci.cancel();
    }
}
