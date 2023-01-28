package retr0.itemfavorites.extension;

import net.minecraft.client.gui.hud.InGameHud;

public interface ExtensionInGameHud {
    void setBookmarkFade(int durationMs);

    static void setBookmarkFade(InGameHud inGameHud, int durationMs) {
        ((ExtensionInGameHud) inGameHud).setBookmarkFade(durationMs);
    }
}
