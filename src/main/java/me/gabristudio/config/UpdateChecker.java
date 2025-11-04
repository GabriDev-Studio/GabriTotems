package me.gabristudio.config;

import me.gabristudio.GabriTotems;
import me.gabristudio.utils.LoggerUtil;
import org.bukkit.Bukkit;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class UpdateChecker {

    private final GabriTotems plugin;
    private final String owner = "GabriDev-Studio";
    private final String repo = "GabriTotems";
    private String latestVersion = null;

    public UpdateChecker(GabriTotems plugin) {
        this.plugin = plugin;
    }

    public void checkForUpdates() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URL url = new URL("https://api.github.com/repos/" + owner + "/" + repo + "/releases/latest");
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(response.toString());
                latestVersion = (String) json.get("tag_name");

                if (latestVersion != null && !latestVersion.equals(plugin.getDescription().getVersion())) {
                    LoggerUtil.log("§e");
                    LoggerUtil.log("§e§lДОСТУПНО ОБНОВЛЕНИЕ!");
                    LoggerUtil.log("§eТекущая версия: §f" + plugin.getDescription().getVersion());
                    LoggerUtil.log("§eНовая версия: §f" + latestVersion);
                    LoggerUtil.log("§eСкачать: §bhttps://github.com/" + owner + "/" + repo + "/releases/latest");
                    LoggerUtil.log("§e");
                } else {
                    LoggerUtil.log("§aВы используете последнюю версию: §f" + plugin.getDescription().getVersion());
                }
            } catch (Exception e) {
                LoggerUtil.warning("Не удалось проверить обновления: " + e.getMessage());
            }
        });
    }

    public boolean isUpdateAvailable() {
        return latestVersion != null && !latestVersion.equals(plugin.getDescription().getVersion());
    }

    public String getLatestVersion() {
        return latestVersion;
    }
}