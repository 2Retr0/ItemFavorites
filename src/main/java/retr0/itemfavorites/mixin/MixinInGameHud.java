package retr0.itemfavorites.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import retr0.itemfavorites.extension.ExtensionInGameHud;
import retr0.itemfavorites.util.RenderUtil;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud implements ExtensionInGameHud {
    @Shadow private int scaledWidth;
    @Shadow private int scaledHeight;

    @Unique private int bookmarkFade;
    @Unique private int bookmarkSlot;
    @Unique private float bookmarkFadeDivisor;

    @Shadow protected abstract PlayerEntity getCameraPlayer();

    @Override
    public void setBookmarkFade(int durationMs) {
        bookmarkFade = durationMs / 100;
        bookmarkSlot = getCameraPlayer().getInventory().selectedSlot;
        bookmarkFadeDivisor = durationMs / 200f;
    }



    @Inject(method = "tick()V", at = @At("TAIL"))
    private void tickBookmarkFade(CallbackInfo ci) {
        if (bookmarkFade > 0) --bookmarkFade;
    }



    /**
     * Renders the bookmark sprite with the alpha only decreasing in the latter half of the initial set time.
     */
    @Inject(method = "renderHotbar", at = @At("TAIL"))
    private void renderBookmark(float tickDelta, DrawContext context, CallbackInfo ci) {
        var selectedSlot = getCameraPlayer().getInventory().selectedSlot;

        if (bookmarkFade == 0 || selectedSlot != bookmarkSlot) return;

        int x = (scaledWidth / 2) - 88 + selectedSlot * 20,
            y = scaledHeight - 19;
        RenderUtil.renderBookmark(context, x, y, bookmarkFade / bookmarkFadeDivisor);
    }
}
