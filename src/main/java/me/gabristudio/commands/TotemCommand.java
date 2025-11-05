package me.gabristudio.commands;

import me.gabristudio.GabriTotems;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TotemCommand implements CommandExecutor {

    private final GabriTotems plugin;

    public TotemCommand(GabriTotems plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("gabritotems.admin")) {
            sender.sendMessage(plugin.getMessageManager().get("no-permission"));
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> {
                plugin.getConfigHandler().reloadConfig();
                sender.sendMessage(plugin.getMessageManager().get("reload"));
            }

            case "give" -> {
                if (args.length != 3) {
                    sender.sendMessage("§cИспользование: /gabritotems give <игрок> <тотем>");
                    return true;
                }
                Player target = plugin.getServer().getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage("§cИгрок не найден: " + args[1]);
                    return true;
                }
                var item = plugin.getTotemManager().createItemStack(args[2]);
                if (item == null) {
                    sender.sendMessage("§cТотем не найден: " + args[2]);
                    return true;
                }
                target.getInventory().addItem(item);
                sender.sendMessage(plugin.getMessageManager().format(
                        "totem-given",
                        "totem", args[2],
                        "player", target.getName()
                ));
            }

            case "list" -> {
                String list = String.join("§7, §e", plugin.getTotemManager().getTotems().keySet());
                if (list.isEmpty()) list = "§7нет";
                sender.sendMessage(plugin.getMessageManager().format("totem-list", "totems", list));
            }

            case "update" -> {
                plugin.getUpdateChecker().checkForUpdates();
                sender.sendMessage("§aПроверка обновлений запущена. Смотрите консоль.");
            }

            default -> sendHelp(sender);
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6=== §lGabriTotems §6===\n" +
                "§e/gabritotems reload §7— перезагрузить конфиг\n" +
                "§e/gabritotems give <игрок> <тотем> §7— выдать тотем\n" +
                "§e/gabritotems list §7— список тотемов\n" +
                "§e/gabritotems update §7— проверить обновления");
    }
}