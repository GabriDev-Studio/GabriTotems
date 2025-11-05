package me.gabristudio.config;

import me.gabristudio.GabriTotems;
import me.gabristudio.utils.LoggerUtil;
import org.bukkit.Bukkit;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {

    private final GabriTotems plugin;
    private final String owner;
    private final String repo;
    private String latestVersion = null;
    private boolean checked = false;

    public UpdateChecker(GabriTotems plugin, String owner, String repo) {
        this.plugin = plugin;
        this.owner = owner;
        this.repo = repo;
    }

    public void checkForUpdates() {
        if (checked) {
            LoggerUtil.log("§eПроверка обновлений уже была проведена.");
            return;
        }
        checked = true;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URL url = new URL("https://api.github.com/repos/" + owner + "/" + repo + "/releases/latest");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();
                if (responseCode != 200) {
                    LoggerUtil.warning("GitHub API вернул код: " + responseCode + ". Возможно, репозиторий приватный или не существует.");
                    return;
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONParser parser = new JSONParser();
                Object obj = parser.parse(response.toString());
                JSONObject json = (JSONObject) obj;
                latestVersion = (String) json.get("tag_name");

                String currentVersion = plugin.getDescription().getVersion();

                if (latestVersion != null && !latestVersion.equals(currentVersion)) {
                    LoggerUtil.log("§e");
                    LoggerUtil.log("§e§lДОСТУПНО ОБНОВЛЕНИЕ!");
                    LoggerUtil.log("§eТекущая версия: §f" + currentVersion);
                    LoggerUtil.log("§eНовая версия: §f" + latestVersion);
                    LoggerUtil.log("§eСкачать: §bhttps://github.com/" + owner + "/" + repo + "/releases/latest");
                    LoggerUtil.log("§e");
                } else if (latestVersion != null) {
                    LoggerUtil.log("§aВы используете последнюю версию: §f" + currentVersion);
                } else {
                    LoggerUtil.warning("Не удалось получить версию с GitHub.");
                }

            } catch (Exception e) {
                LoggerUtil.warning("Ошибка проверки обновлений: " + e.getMessage());
                LoggerUtil.log("§7Возможные причины: Нет интернета, репозиторий не найден, API GitHub недоступен.");
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