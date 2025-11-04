package me.gabristudio.utils;

import me.gabristudio.GabriTotems;
import org.bukkit.ChatColor;

public class LoggerUtil {
    private static GabriTotems plugin;

    public static void init(GabriTotems instance) {
        plugin = instance;
    }

    public static void log(String msg) {
        if (plugin != null) plugin.getLogger().info(ChatColor.translateAlternateColorCodes('&', msg));
    }

    public static void warning(String msg) {
        if (plugin != null) plugin.getLogger().warning(ChatColor.translateAlternateColorCodes('&', msg));
    }
}