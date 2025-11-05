package me.gabristudio;

import me.gabristudio.commands.TotemCommand;
import me.gabristudio.config.ConfigHandler;
import me.gabristudio.config.MessageManager;
import me.gabristudio.config.UpdateChecker;
import me.gabristudio.events.TotemListener;
import me.gabristudio.totems.TotemManager;
import me.gabristudio.utils.LoggerUtil;
import org.bukkit.plugin.java.JavaPlugin;

public final class GabriTotems extends JavaPlugin {

    private ConfigHandler configHandler;
    private MessageManager messageManager;
    private TotemManager totemManager;
    private UpdateChecker updateChecker;
    private boolean isPAPIEnabled = false;

    @Override
    public void onEnable() {
        LoggerUtil.init(this);

        LoggerUtil.log("§a=== GabriTotems v1.0 ===");
        LoggerUtil.log("§7Запуск плагина...");

        saveDefaultConfig();
        configHandler = new ConfigHandler(this);
        messageManager = new MessageManager(this);
        totemManager = new TotemManager(this);

        // Проверка PAPI
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            isPAPIEnabled = true;
            LoggerUtil.log("§aPlaceholderAPI найден! Плейсхолдеры включены.");
        } else {
            isPAPIEnabled = false;
            LoggerUtil.warning("PlaceholderAPI не найден. Плейсхолдеры отключены.");
        }

        getServer().getPluginManager().registerEvents(new TotemListener(this), this);
        getCommand("gabritotems").setExecutor(new TotemCommand(this));

        // ИСПРАВЛЕНО: Передаём owner и repo
        updateChecker = new UpdateChecker(this, "GabriDev-Studio", "GabriTotems");
        if (getConfig().getBoolean("check-updates", true)) {
            updateChecker.checkForUpdates();
        }

        LoggerUtil.log("§aПлагин успешно запущен!");
    }

    @Override
    public void onDisable() {
        LoggerUtil.log("§cПлагин выключен.");
    }

    public ConfigHandler getConfigHandler() { return configHandler; }
    public MessageManager getMessageManager() { return messageManager; }
    public TotemManager getTotemManager() { return totemManager; }
    public UpdateChecker getUpdateChecker() { return updateChecker; }
    public boolean isPAPIEnabled() { return isPAPIEnabled; }
}