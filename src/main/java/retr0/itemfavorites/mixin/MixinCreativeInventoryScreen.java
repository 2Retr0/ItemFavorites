package retr0.itemfavorites.mixin;

import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeInventoryScreen.class)
public class MixinCreativeInventoryScreen<T extends ScreenHandler> extends MixinHandledScreen<T> {
    /**
     * @see MixinHandledScreen#handleFavoriteItemInteractions(Slot, int, int, SlotActionType, CallbackInfo)
     */
    @Inject(
        method = "onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V",
        at = @At("HEAD"),
        cancellable = true)
    protected void handleFavoriteItemInteractions(
        Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci)
    {
        super.handleFavoriteItemInteractions(slot, slotId, button, actionType, ci);
    }

    protected MixinCreativeInventoryScreen(Text title) {
        super(title);
    }
}
