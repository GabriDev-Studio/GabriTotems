package me.gabristudio.totems;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;

import java.util.List;
import java.util.Map;

public class CustomTotem {

    private final String id;
    private final Material material;
    private final int customModelData;
    private final String displayName;
    private final List<String> lore;
    private final Map<Enchantment, Integer> enchantments;
    private final boolean autoUse;
    private final String trigger;
    private final int cooldown;
    private final double healthRestore;
    private final String message;
    private final String actionbar;
    private final String title;
    private final String subtitle;
    private final Sound sound;
    private final String particle;
    private final int particleCount;
    private final double radius;
    private final List<String> effects;
    private final List<String> commands;
    private final List<String> giveItems;
    private final String teleport;
    private final Curse curse;

    public CustomTotem(String id, Material material, int customModelData, String displayName, List<String> lore,
                       Map<Enchantment, Integer> enchantments, boolean autoUse, String trigger, int cooldown,
                       double healthRestore, String message, String actionbar, String title, String subtitle,
                       Sound sound, String particle, int particleCount, double radius, List<String> effects,
                       List<String> commands, List<String> giveItems, String teleport, Curse curse) {
        this.id = id;
        this.material = material;
        this.customModelData = customModelData;
        this.displayName = displayName;
        this.lore = lore;
        this.enchantments = enchantments;
        this.autoUse = autoUse;
        this.trigger = trigger;
        this.cooldown = cooldown;
        this.healthRestore = healthRestore;
        this.message = message;
        this.actionbar = actionbar;
        this.title = title;
        this.subtitle = subtitle;
        this.sound = sound;
        this.particle = particle;
        this.particleCount = particleCount;
        this.radius = radius;
        this.effects = effects;
        this.commands = commands;
        this.giveItems = giveItems;
        this.teleport = teleport;
        this.curse = curse;
    }

    // Геттеры
    public String getId() { return id; }
    public Material getMaterial() { return material; }
    public int getCustomModelData() { return customModelData; }
    public String getDisplayName() { return displayName; }
    public List<String> getLore() { return lore; }
    public Map<Enchantment, Integer> getEnchantments() { return enchantments; }
    public boolean isAutoUse() { return autoUse; }
    public String getTrigger() { return trigger; }
    public int getCooldown() { return cooldown; }
    public double getHealthRestore() { return healthRestore; }
    public String getMessage() { return message; }
    public String getActionbar() { return actionbar; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public Sound getSound() { return sound; }
    public String getParticle() { return particle; }
    public int getParticleCount() { return particleCount; }
    public double getRadius() { return radius; }
    public List<String> getEffects() { return effects; }
    public List<String> getCommands() { return commands; }
    public List<String> getGiveItems() { return giveItems; }
    public String getTeleport() { return teleport; }
    public Curse getCurse() { return curse; }

    // ДОБАВЛЕНО: Проверка включён ли тотем
    public boolean isEnabled() {
        return true; // Все загруженные тотемы включены (фильтрация в loadTotems)
    }

    // Вложенный класс Curse
    public static class Curse {
        private final boolean enabled;
        private final double radius;
        private final List<String> effects;
        private final String message;

        public Curse(boolean enabled, double radius, List<String> effects, String message) {
            this.enabled = enabled;
            this.radius = radius;
            this.effects = effects;
            this.message = message;
        }

        public boolean isEnabled() { return enabled; }
        public double getRadius() { return radius; }
        public List<String> getEffects() { return effects; }
        public String getMessage() { return message; }
    }
}