package me.gabristudio.totems;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;

import java.util.List;
import java.util.Map;

public class CustomTotem {
    private final String id;
    private Material material;
    private int customModelData = -1;
    private String displayName;
    private List<String> lore;
    private Map<Enchantment, Integer> enchantments;
    private boolean autoUse;
    private String trigger;
    private int cooldown;
    private double healthRestore;
    private String message, actionbar, title, subtitle;
    private Sound sound;
    private String particle;
    private int particleCount;
    private double radius;
    private List<String> effects;
    private List<String> commands;
    private List<String> giveItems;
    private String teleport;
    private Curse curse;

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

    // Геттеры (ты ток за сюда зашёл? :) Думаешь гпт???)
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

    public static class Curse {
        private boolean enabled;
        private double radius;
        private List<String> effects;
        private String message;

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