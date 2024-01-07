package retr0.itemfavorites.extension;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.InGameHud;

@Environment(EnvType.CLIENT)
public interface ExtensionInGameHud {
    void itemFavorites$setBookmarkFade(int durationMs);

    static void setBookmarkFade(InGameHud inGameHud, int durationMs) {
        ((ExtensionInGameHud) inGameHud).itemFavorites$setBookmarkFade(durationMs);
    }
}
