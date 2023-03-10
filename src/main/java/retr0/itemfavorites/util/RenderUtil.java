package retr0.itemfavorites.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Unique;

import static retr0.itemfavorites.ItemFavorites.MOD_ID;

public class RenderUtil {
    @Unique private static final Identifier BOOKMARK_TEXTURE = new Identifier(MOD_ID, "textures/gui/bookmark.png");

    public static void renderBookmark(MatrixStack matrices, int x, int y, float alpha) {
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, BOOKMARK_TEXTURE);

        // Draw bookmark shadow
        RenderSystem.setShaderColor(0f, 0f, 0f, alpha * 0.25f);
        matrices.push();
        matrices.translate(0.5f, 0.5f, 0f);
        DrawableHelper.drawTexture(matrices, x, y, 0, 0, 16, 16, 16, 16);
        matrices.pop();

        // Draw bookmark
        RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
        DrawableHelper.drawTexture(matrices, x, y, 0, 0, 16, 16, 16, 16);

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }
}
