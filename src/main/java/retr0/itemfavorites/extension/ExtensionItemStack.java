package retr0.itemfavorites.extension;

import net.minecraft.item.ItemStack;

public interface ExtensionItemStack {
    boolean isFavorite();

    void setFavorite(boolean status);

    static boolean isFavorite(ItemStack itemStack) {
        return ((ExtensionItemStack) (Object) itemStack).isFavorite();
    }

    static void setFavorite(ItemStack itemStack, boolean status) {
        ((ExtensionItemStack) (Object) itemStack).setFavorite(status);
    }
}
