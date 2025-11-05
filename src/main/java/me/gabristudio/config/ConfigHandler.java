package me.gabristudio.config;

import me.gabristudio.GabriTotems;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigHandler {

    private final GabriTotems plugin;
    private File configFile;
    private FileConfiguration config;

    public ConfigHandler(GabriTotems plugin) {
        this.plugin = plugin;
        setupConfig();
    }

    private void setupConfig() {
        configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        checkUpdate();
    }

    private void checkUpdate() {
        int version = config.getInt("config-version", 1);
        if (version < 2) {
            config.set("config-version", 2);
            saveConfig();
        }
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
        plugin.getTotemManager().reloadTotems();
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Ошибка сохранения config.yml: " + e.getMessage());
        }
    }

    public FileConfiguration getConfig() { return config; }
}