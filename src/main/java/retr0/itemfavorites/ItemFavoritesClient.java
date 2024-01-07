package retr0.itemfavorites;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import retr0.itemfavorites.network.PacketRegistry;

import static retr0.itemfavorites.ItemFavorites.MOD_ID;

public class ItemFavoritesClient implements ClientModInitializer {
    public static final KeyBinding FAVORITE_MODIFIER_BINDING = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        "key." + MOD_ID + ".favorite_item",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_LEFT_ALT,
        "key.categories.inventory"));

    @Override
    public void onInitializeClient() {
        PacketRegistry.registerS2CPackets();
    }
}
