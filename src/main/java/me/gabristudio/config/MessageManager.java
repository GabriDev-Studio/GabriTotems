package me.gabristudio.config;

import me.gabristudio.GabriTotems;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class MessageManager {

    private final GabriTotems plugin;
    private FileConfiguration messages;

    public MessageManager(GabriTotems plugin) {
        this.plugin = plugin;
        loadMessages();
    }

    private void loadMessages() {
        File file = new File(plugin.getDataFolder(), "messages_ru.yml");
        if (!file.exists()) {
            plugin.saveResource("messages_ru.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(file);
    }

    public String get(String path) {
        String msg = messages.getString(path);
        return msg != null ? ChatColor.translateAlternateColorCodes('&', msg) : path;
    }

    public String format(String path, Object... args) {
        String msg = get(path);
        for (int i = 0; i < args.length; i += 2) {
            msg = msg.replace("{" + args[i] + "}", args[i + 1].toString());
        }
        return msg;
    }
}
