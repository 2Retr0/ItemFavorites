package retr0.itemfavorites;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retr0.itemfavorites.network.PacketRegistry;

public class ItemFavorites implements ModInitializer {
	public static final String MOD_ID = "itemfavorites";
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final KeyBinding favoriteModifierBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key." + MOD_ID + ".favorite_item",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_LEFT_ALT,
			"key.categories.inventory"));

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		LOGGER.info("Initialized ItemFavorites!");
		PacketRegistry.registerC2SPackets();
	}
}
