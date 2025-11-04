package me.gabristudio.totems;

import me.gabristudio.GabriTotems;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TotemManager {

    private final GabriTotems plugin;
    private final Map<String, CustomTotem> totems = new HashMap<>();
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public TotemManager(GabriTotems plugin) {
        this.plugin = plugin;
        loadTotems();
    }

    public void reloadTotems() {
        loadTotems();
    }

    private void loadTotems() {
        totems.clear();
        var section = plugin.getConfig().getConfigurationSection("custom-totems");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            var totemSection = section.getConfigurationSection(key);
            if (!totemSection.getBoolean("enabled", false)) continue;

            Material material = Material.getMaterial(totemSection.getString("item", "TOTEM_OF_UNDYING").toUpperCase());
            if (material == null) continue;

            int customModelData = "none".equalsIgnoreCase(totemSection.getString("custom-model-data", "none")) ? -1 : totemSection.getInt("custom-model-data");

            CustomTotem totem = new CustomTotem(
                    key,
                    material,
                    customModelData,
                    totemSection.getString("display-name", ""),
                    totemSection.getStringList("lore"),
                    parseEnchantments(totemSection.getConfigurationSection("enchantments")),
                    totemSection.getBoolean("auto-use", false),
                    totemSection.getString("trigger", "DAMAGE"),
                    totemSection.getInt("cooldown", 0),
                    totemSection.getDouble("health-restore", 1.0),
                    totemSection.getString("message", ""),
                    totemSection.getString("actionbar", ""),
                    totemSection.getString("title", ""),
                    totemSection.getString("subtitle", ""),
                    totemSection.getString("sound", "NONE").equals("NONE") ? null : org.bukkit.Sound.valueOf(totemSection.getString("sound").toUpperCase()),
                    totemSection.getString("particle", "NONE"),
                    totemSection.getInt("particle-count", 20),
                    totemSection.getDouble("radius", 5.0),
                    totemSection.getStringList("effects"),
                    totemSection.getStringList("commands"),
                    totemSection.getStringList("give-items"),
                    totemSection.getString("teleport", "NONE"),
                    parseCurse(totemSection.getConfigurationSection("curse"))
            );

            totems.put(key, totem);
        }
    }

    private Map<org.bukkit.enchantments.Enchantment, Integer> parseEnchantments(org.bukkit.configuration.ConfigurationSection section) {
        Map<org.bukkit.enchantments.Enchantment, Integer> enchants = new HashMap<>();
        if (section == null) return enchants;

        for (String key : section.getKeys(false)) {
            org.bukkit.enchantments.Enchantment ench = org.bukkit.enchantments.Enchantment.getByName(key.toUpperCase());
            if (ench != null) {
                enchants.put(ench, section.getInt(key));
            }
        }
        return enchants;
    }

    private CustomTotem.Curse parseCurse(org.bukkit.configuration.ConfigurationSection section) {
        if (section == null || !section.getBoolean("enabled", false)) return null;

        return new CustomTotem.Curse(
                section.getBoolean("enabled"),
                section.getDouble("radius", 5.0),
                section.getStringList("effects"),
                section.getString("message-to-victim", "")
        );
    }

    public ItemStack createItemStack(String totemId) {
        CustomTotem totem = totems.get(totemId);
        if (totem == null) return null;

        ItemStack item = new ItemStack(totem.getMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.setDisplayName(totem.getDisplayName());
        meta.setLore(totem.getLore());
        if (totem.getCustomModelData() != -1) {
            meta.setCustomModelData(totem.getCustomModelData());
        }
        totem.getEnchantments().forEach((ench, level) -> meta.addEnchant(ench, level, true));

        NamespacedKey key = new NamespacedKey(plugin, "totem_id");
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, totem.getId());

        item.setItemMeta(meta);
        return item;
    }

    public boolean matches(ItemStack item, CustomTotem totem) {
        if (item == null || item.getType() != totem.getMaterial()) return false;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return totem.getCustomModelData() == -1;

        if (totem.getCustomModelData() != -1 && (!meta.hasCustomModelData() || meta.getCustomModelData() != totem.getCustomModelData())) return false;

        NamespacedKey key = new NamespacedKey(plugin, "totem_id");
        return meta.getPersistentDataContainer().get(key, PersistentDataType.STRING).equals(totem.getId());
    }

    public boolean isOnCooldown(UUID player, String totemId) {
        Long end = cooldowns.get(player);
        if (end == null) return false;
        return System.currentTimeMillis() < end;
    }

    public long getRemainingCooldown(Player player, String totemId) {
        Long end = cooldowns.get(player.getUniqueId());
        if (end == null) return 0;
        return Math.max(0, (end - System.currentTimeMillis()) / 1000);
    }

    public void setCooldown(UUID player, String totemId) {
        CustomTotem totem = totems.get(totemId);
        if (totem == null) return;
        cooldowns.put(player, System.currentTimeMillis() + (totem.getCooldown() * 1000L));
    }

    public Map<String, CustomTotem> getTotems() {
        return totems;
    }
}