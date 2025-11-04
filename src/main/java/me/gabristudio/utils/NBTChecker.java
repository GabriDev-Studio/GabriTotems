package me.gabristudio.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class NBTChecker {
    public static String getString(ItemStack item, String key) {
        if (item == null || !item.hasItemMeta()) return null;
        ItemMeta meta = item.getItemMeta();
        NamespacedKey nsKey = new NamespacedKey("gabritotems", key);
        return meta.getPersistentDataContainer().get(nsKey, PersistentDataType.STRING);
    }
}