package me.gabristudio.totems;

import me.gabristudio.GabriTotems;
import me.gabristudio.utils.LoggerUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class TotemManager {

    private final GabriTotems plugin;
    private final Map<String, CustomTotem> totems = new HashMap<>();
    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    public TotemManager(GabriTotems plugin) {
        this.plugin = plugin;
        loadTotems();
    }

    public void reloadTotems() {
        totems.clear();
        cooldowns.clear();
        loadTotems();
        LoggerUtil.log("§aТотемы перезагружены.");
    }

    private void loadTotems() {
        var section = plugin.getConfig().getConfigurationSection("custom-totems");
        if (section == null) {
            LoggerUtil.error("Секция custom-totems не найдена в config.yml!");
            return;
        }

        for (String key : section.getKeys(false)) {
            var totemSection = section.getConfigurationSection(key);

            if (!totemSection.getBoolean("enabled", true)) {
                LoggerUtil.log("§eТотем §f" + key + " §eотключён (enabled: false)");
                continue;
            }

            try {
                Material material = Material.valueOf(totemSection.getString("item", "TOTEM_OF_UNDYING").toUpperCase());
                int cmd = "none".equalsIgnoreCase(totemSection.getString("custom-model-data", "none"))
                        ? -1 : totemSection.getInt("custom-model-data");

                CustomTotem totem = new CustomTotem(
                        key,
                        material,
                        cmd,
                        ChatColor.translateAlternateColorCodes('&', totemSection.getString("display-name", "")),
                        totemSection.getStringList("lore").stream()
                                .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                                .toList(),
                        parseEnchantments(totemSection.getConfigurationSection("enchantments")),
                        totemSection.getBoolean("auto-use", false),
                        totemSection.getString("trigger", "DAMAGE").toUpperCase(),
                        totemSection.getInt("cooldown", 0),
                        totemSection.getDouble("health-restore", 1.0),
                        ChatColor.translateAlternateColorCodes('&', totemSection.getString("message", "")),
                        ChatColor.translateAlternateColorCodes('&', totemSection.getString("actionbar", "")),
                        ChatColor.translateAlternateColorCodes('&', totemSection.getString("title", "")),
                        ChatColor.translateAlternateColorCodes('&', totemSection.getString("subtitle", "")),
                        totemSection.contains("sound") ? org.bukkit.Sound.valueOf(totemSection.getString("sound").toUpperCase()) : null,
                        totemSection.getString("particle", "NONE").toUpperCase(),
                        totemSection.getInt("particle-count", 20),
                        totemSection.getDouble("radius", 5.0),
                        totemSection.getStringList("effects"),
                        totemSection.getStringList("commands"),
                        totemSection.getStringList("give-items"),
                        totemSection.getString("teleport", "NONE").toUpperCase(),
                        parseCurse(totemSection.getConfigurationSection("curse"))
                );

                totems.put(key, totem);
                LoggerUtil.log("§aЗагружен тотем: §f" + key);

            } catch (Exception e) {
                LoggerUtil.error("Ошибка загрузки тотема " + key + ": " + e.getMessage());
            }
        }
    }

    private Map<Enchantment, Integer> parseEnchantments(org.bukkit.configuration.ConfigurationSection section) {
        Map<Enchantment, Integer> ench = new HashMap<>();
        if (section == null) return ench;
        for (String key : section.getKeys(false)) {
            Enchantment enchType = Enchantment.getByKey(org.bukkit.NamespacedKey.minecraft(key.toLowerCase()));
            if (enchType != null) {
                ench.put(enchType, section.getInt(key));
            }
        }
        return ench;
    }

    private CustomTotem.Curse parseCurse(org.bukkit.configuration.ConfigurationSection section) {
        if (section == null || !section.getBoolean("enabled", false)) return null;
        return new CustomTotem.Curse(
                true,
                section.getDouble("radius", 10.0),
                section.getStringList("effects"),
                ChatColor.translateAlternateColorCodes('&', section.getString("message-to-victim", ""))
        );
    }

    public ItemStack createItemStack(String totemId) {
        CustomTotem totem = totems.get(totemId);
        if (totem == null) {
            LoggerUtil.warning("Попытка выдать несуществующий тотем: " + totemId);
            return null;
        }

        ItemStack item = new ItemStack(totem.getMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.setDisplayName(totem.getDisplayName());
        meta.setLore(totem.getLore());
        if (totem.getCustomModelData() != -1) meta.setCustomModelData(totem.getCustomModelData());
        totem.getEnchantments().forEach((e, l) -> meta.addEnchant(e, l, true));

        NamespacedKey key = new NamespacedKey(plugin, "totem_id");
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, totem.getId());
        item.setItemMeta(meta);

        LoggerUtil.log("§aВыдан тотем: §f" + totemId);
        return item;
    }

    public boolean matches(ItemStack item, String totemId) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(plugin, "totem_id");
        String id = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        return totemId.equals(id);
    }

    public CustomTotem getTotemById(String id) { return totems.get(id); }

    public boolean isOnCooldown(Player player, String totemId) {
        Map<String, Long> playerCooldowns = cooldowns.getOrDefault(player.getUniqueId(), new HashMap<>());
        Long end = playerCooldowns.get(totemId);
        return end != null && System.currentTimeMillis() < end;
    }

    public long getRemainingCooldown(Player player, String totemId) {
        Map<String, Long> playerCooldowns = cooldowns.getOrDefault(player.getUniqueId(), new HashMap<>());
        Long end = playerCooldowns.get(totemId);
        if (end == null) return 0;
        return Math.max(0, (end - System.currentTimeMillis()) / 1000);
    }

    public void setCooldown(Player player, String totemId, int seconds) {
        cooldowns.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                .put(totemId, System.currentTimeMillis() + (seconds * 1000L));
    }

    public Map<String, CustomTotem> getTotems() { return totems; }
}