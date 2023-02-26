package retr0.itemfavorites.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import retr0.itemfavorites.extension.ExtensionItemStack;
import retr0.itemfavorites.network.SyncFavoriteItemsC2SPacket;
import retr0.itemfavorites.util.RenderUtil;

import java.util.Set;

import static net.minecraft.screen.slot.SlotActionType.PICKUP;

@Mixin(HandledScreen.class)
public class MixinHandledScreen<T extends ScreenHandler> extends Screen {
    @Shadow @Final protected T handler;
    @Shadow @Final protected Set<Slot> cursorDragSlots;

    /**
     * Handles changing the favorite status of item stacks when the favorite shortcut is pressed.
     */
    @SuppressWarnings("InvalidInjectorMethodSignature")
    @Inject(
        method = "mouseClicked",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/Util;getMeasuringTimeMs()J"),
        locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void handleFavoriteShortcutKeystroke(
        double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir, boolean bl, Slot slot)
    {
        if (slot == null || !slot.hasStack()) return;

        @SuppressWarnings("DataFlowIssue") // Client is non-null while in-game.
        var isFavoriteShortcutPressed =
            button == 0 && InputUtil.isKeyPressed(client.getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_ALT);
        var isSlotPlayerOwned = slot.inventory instanceof PlayerInventory;

        if (!isFavoriteShortcutPressed || !isSlotPlayerOwned) return;

        var slotStack = slot.getStack();
        var toggledStatus = !ExtensionItemStack.isFavorite(slotStack);

        ExtensionItemStack.setFavorite(slotStack, toggledStatus);
        SyncFavoriteItemsC2SPacket.send(handler.syncId, slot.id, toggledStatus); // Sync changed status with the server.

        // noinspection DataFlowIssue // Client and player are non-null while in-game.
        client.player.playSound(toggledStatus ? SoundEvents.BLOCK_BONE_BLOCK_FALL : SoundEvents.BLOCK_BONE_BLOCK_BREAK,
            SoundCategory.BLOCKS, 0.25f, client.player.clientWorld.random.nextFloat() * 0.1f + 0.9f);

        cir.setReturnValue(true);
        cir.cancel();
    }



    /**
     * Renders the bookmark texture on slots containing favorite items.
     */
    @Inject(method = "drawSlot", at = @At("TAIL"))
    private void renderSlotBookmark(MatrixStack matrices, Slot slot, CallbackInfo ci) {
        if (!ExtensionItemStack.isFavorite(slot.getStack())) return;

        int x = slot.x, y = slot.y - 1;
        RenderUtil.renderBookmark(matrices, x, y, 1f);
    }



    /**
     * Suppresses some actions on favorite items and implements some conditions for when favorite items should be
     * created/destroyed (other conditions are handled in {@link MixinItemStack}, {@link MixinScreenHandler}, and
     * {@link MixinSlot}).
     */
    @Inject(
        method = "onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;clickSlot(IIILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V"),
        cancellable = true)
    private void handleFavoriteItemInteractions(
        Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci)
    {
        var cursorStack = handler.getCursorStack();
        var cursorHasFavorite = ExtensionItemStack.isFavorite(cursorStack);

        if (slot == null) {
            // Cancel dropping a held favorite item (i.e. clicking outside the inventory screen).
            if (actionType == PICKUP && slotId == ScreenHandler.EMPTY_SPACE_SLOT_INDEX && cursorHasFavorite)
                ci.cancel();
            return;
        }

        var slotStack = slot.getStack();
        var slotHasFavorite = ExtensionItemStack.isFavorite(slotStack);
        var isSlotPlayerOwned = slot.inventory instanceof PlayerInventory;

        var depositCount = button != 0 ? 1 : cursorStack.getCount();
        var canFullyMerge = ItemStack.canCombine(cursorStack, slotStack)
            && slotStack.getMaxCount() >= slotStack.getCount() + depositCount
            && cursorStack.getCount() == depositCount;

        switch (actionType) {
            case PICKUP -> {
                if (cursorHasFavorite && !isSlotPlayerOwned)
                    ExtensionItemStack.setFavorite(cursorStack, false);
                else if (cursorHasFavorite && canFullyMerge)
                    ExtensionItemStack.setFavorite(slotStack, true);
            }
            case QUICK_MOVE -> {
                // Cancel shift-clicking any favorite item.
                if (slotHasFavorite) ci.cancel();
            }
            case SWAP -> {
                @SuppressWarnings("DataFlowIssue") // Client and player are non-null while in-game.
                var hotbarStack = client.player.getInventory().getStack(button);
                // Cancel swapping a hotbar favorite item with a slot outside the player inventory.
                if (ExtensionItemStack.isFavorite(hotbarStack) && !isSlotPlayerOwned )
                    ci.cancel();
            }
            case THROW -> {
                // Cancel dropping a favorite item (i.e. pressing 'q').
                if (slotHasFavorite || cursorHasFavorite) ci.cancel();
            }
            case QUICK_CRAFT -> {
                if (cursorHasFavorite && canFullyMerge && cursorDragSlots.size() == 1)
                    ExtensionItemStack.setFavorite(slotStack, true);
            }
        }
    }

    protected MixinHandledScreen(Text title) { super(title); }
}
