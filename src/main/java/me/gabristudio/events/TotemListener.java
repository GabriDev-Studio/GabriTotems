package me.gabristudio.events;

import me.gabristudio.GabriTotems;
import me.gabristudio.totems.CustomTotem;
import me.gabristudio.utils.LoggerUtil;
import me.gabristudio.utils.ParticleAnimator;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class TotemListener implements Listener {

    private final GabriTotems plugin;

    public TotemListener(GabriTotems plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        CustomTotem totem = findTotem(player, "DEATH");
        if (totem != null && activateTotem(player, totem)) {
            // НЕЛЬЗЯ: e.setCancelled(true);
            // Вместо этого — воскрешаем вручную
            e.setKeepInventory(true);
            e.setKeepLevel(true);
            e.getDrops().clear();
            e.setDeathMessage(null); // Убираем сообщение о смерти

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.spigot().respawn();
                player.setHealth(1.0);
                LoggerUtil.log("§aТотем воскресил игрока: " + player.getName());
            }, 1L);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        if (player.getHealth() - e.getFinalDamage() > 0) return;

        CustomTotem totem = findTotem(player, "DAMAGE");
        if (totem != null && activateTotem(player, totem)) {
            e.setCancelled(true);
            player.setHealth(1.0);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (!e.getAction().name().contains("RIGHT_CLICK")) return;
        Player player = e.getPlayer();
        ItemStack item = e.getItem();
        if (item == null) return;

        for (CustomTotem totem : plugin.getTotemManager().getTotems().values()) {
            if (!totem.getTrigger().equals("INTERACT")) continue;
            if (plugin.getTotemManager().matches(item, totem.getId()) && activateTotem(player, totem)) {
                item.setAmount(item.getAmount() - 1);
                break;
            }
        }
    }

    private CustomTotem findTotem(Player player, String trigger) {
        for (CustomTotem totem : plugin.getTotemManager().getTotems().values()) {
            if (!totem.getTrigger().equalsIgnoreCase(trigger)) continue;
            ItemStack item = getTotemItem(player, totem);
            if (item != null && plugin.getTotemManager().matches(item, totem.getId())) {
                return totem;
            }
        }
        return null;
    }

    private ItemStack getTotemItem(Player player, CustomTotem totem) {
        if (totem.isAutoUse()) {
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && plugin.getTotemManager().matches(item, totem.getId())) return item;
            }
        } else {
            ItemStack main = player.getInventory().getItemInMainHand();
            ItemStack off = player.getInventory().getItemInOffHand();
            if (plugin.getTotemManager().matches(main, totem.getId())) return main;
            if (plugin.getTotemManager().matches(off, totem.getId())) return off;
        }
        return null;
    }

    private boolean activateTotem(Player player, CustomTotem totem) {
        if (plugin.getTotemManager().isOnCooldown(player, totem.getId())) {
            long time = plugin.getTotemManager().getRemainingCooldown(player, totem.getId());
            player.sendMessage("§cКулдаун: §e" + time + " сек");
            return false;
        }

        ItemStack item = getTotemItem(player, totem);
        if (item != null && totem.isAutoUse()) {
            item.setAmount(item.getAmount() - 1);
        }

        plugin.getTotemManager().setCooldown(player, totem.getId(), totem.getCooldown());

        // Эффекты
        if (!totem.getMessage().isEmpty()) player.sendMessage(totem.getMessage());
        if (!totem.getActionbar().isEmpty()) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(totem.getActionbar()));
        }
        if (!totem.getTitle().isEmpty()) {
            player.sendTitle(totem.getTitle(), totem.getSubtitle(), 10, 70, 20);
        }
        if (totem.getSound() != null) player.getWorld().playSound(player.getLocation(), totem.getSound(), 1, 1);
        if (!"NONE".equals(totem.getParticle())) {
            ParticleAnimator.spawnCircle(player, totem.getParticle(), totem.getParticleCount(), totem.getRadius());
        }

        player.setHealth(Math.min(player.getHealth() + totem.getHealthRestore(), 20));
        for (String eff : totem.getEffects()) applyEffect(player, eff);
        if (totem.getCurse() != null && totem.getCurse().isEnabled()) applyCurse(player, totem);
        executeCommands(player, totem);
        giveItems(player, totem);
        teleportPlayer(player, totem);

        LoggerUtil.log("§a" + player.getName() + " активировал тотем: §f" + totem.getId());
        return true;
    }

    private void applyEffect(Player p, String eff) {
        String[] parts = eff.split(":");
        if (parts.length != 3) return;
        PotionEffectType type = PotionEffectType.getByName(parts[0].toUpperCase());
        if (type != null) p.addPotionEffect(new PotionEffect(type, Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
    }

    private void applyCurse(Player activator, CustomTotem totem) {
        int count = 0;
        for (Player t : activator.getWorld().getPlayers()) {
            if (t.equals(activator)) continue;
            if (t.getLocation().distance(activator.getLocation()) > totem.getCurse().getRadius()) continue;
            for (String e : totem.getCurse().getEffects()) applyEffect(t, e);
            if (!totem.getCurse().getMessage().isEmpty()) t.sendMessage(totem.getCurse().getMessage());
            count++;
        }
        if (count > 0) activator.sendMessage("§cПроклято: §e" + count + " игроков");
    }

    private void executeCommands(Player p, CustomTotem t) {
        for (String cmd : t.getCommands()) {
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmd.replace("%player%", p.getName()));
        }
    }

    private void giveItems(Player p, CustomTotem t) {
        for (String give : t.getGiveItems()) {
            String[] parts = give.split(" ");
            p.getInventory().addItem(new ItemStack(Material.valueOf(parts[0].toUpperCase()), Integer.parseInt(parts[1])));
        }
    }

    private void teleportPlayer(Player p, CustomTotem t) {
        if ("SPAWN".equals(t.getTeleport())) p.teleport(p.getWorld().getSpawnLocation());
    }
}