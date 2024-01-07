package retr0.itemfavorites.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Unique;

import static retr0.itemfavorites.ItemFavorites.MOD_ID;

@Environment(EnvType.CLIENT)
public class RenderUtil {
    @Unique private static final Identifier BOOKMARK_TEXTURE = new Identifier(MOD_ID, "textures/gui/bookmark.png");

    public static void renderBookmark(DrawContext context, int x, int y, float alpha) {
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        var matrices = context.getMatrices();
        // Draw bookmark shadow
        RenderSystem.setShaderColor(0f, 0f, 0f, alpha * 0.25f);
        matrices.push();
        matrices.translate(0.5f, 0.5f, 0f);
        context.drawTexture(BOOKMARK_TEXTURE, x, y, 0, 0, 16, 16, 16, 16);
        matrices.pop();

        // Draw bookmark
        RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
        context.drawTexture(BOOKMARK_TEXTURE, x, y, 0, 0, 16, 16, 16, 16);

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }
}
