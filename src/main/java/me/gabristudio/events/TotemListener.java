package me.gabristudio.events;

import me.gabristudio.GabriTotems;
import me.gabristudio.totems.CustomTotem;
import me.gabristudio.utils.LoggerUtil;
import me.gabristudio.utils.ParticleAnimator;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class TotemListener implements Listener {

    private final GabriTotems plugin;

    public TotemListener(GabriTotems plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        if (player.getHealth() - e.getFinalDamage() > 0) return;

        CustomTotem totem = findTotem(player, "DAMAGE");
        if (totem != null && activateTotem(player, totem)) {
            e.setCancelled(true);
        }
    }

    private CustomTotem findTotem(Player player, String trigger) {
        for (CustomTotem totem : plugin.getTotemManager().getTotems().values()) {
            if (!totem.getTrigger().equalsIgnoreCase(trigger)) continue;
            ItemStack item = getItemInUse(player, totem);
            if (item != null && plugin.getTotemManager().matches(item, totem)) {
                return totem;
            }
        }
        return null;
    }

    private ItemStack getItemInUse(Player player, CustomTotem totem) {
        if (totem.isAutoUse()) {
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && plugin.getTotemManager().matches(item, totem)) return item;
            }
        } else {
            ItemStack main = player.getInventory().getItemInMainHand();
            ItemStack off = player.getInventory().getItemInOffHand();
            if (plugin.getTotemManager().matches(main, totem)) return main;
            if (plugin.getTotemManager().matches(off, totem)) return off;
        }
        return null;
    }

    private boolean activateTotem(Player player, CustomTotem totem) {
        if (plugin.getTotemManager().isOnCooldown(player.getUniqueId(), totem.getId())) {
            long time = plugin.getTotemManager().getRemainingCooldown(player, totem.getId());
            String msg = plugin.getMessageManager().get("cooldown").replace("%time%", String.valueOf(time));
            player.sendMessage(msg);
            return false;
        }

        ItemStack item = getItemInUse(player, totem);
        if (item != null && totem.isAutoUse()) {
            item.setAmount(item.getAmount() - 1);
        }

        plugin.getTotemManager().setCooldown(player.getUniqueId(), totem.getId());

        // Эффекты
        if (!totem.getMessage().isEmpty()) player.sendMessage(totem.getMessage());
        if (!totem.getActionbar().isEmpty()) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(totem.getActionbar()));
        }
        if (!totem.getTitle().isEmpty()) {
            player.sendTitle(totem.getTitle(), totem.getSubtitle(), 10, 70, 20);
        }

        if (totem.getSound() != null) {
            player.getWorld().playSound(player.getLocation(), totem.getSound(), 1.0f, 1.0f);
        }
        if (!"NONE".equals(totem.getParticle())) {
            ParticleAnimator.spawnCircle(player, totem.getParticle(), totem.getParticleCount(), totem.getRadius());
        }

        player.setHealth(Math.min(player.getHealth() + totem.getHealthRestore(), player.getMaxHealth()));
        for (String eff : totem.getEffects()) applyEffect(player, eff);

        if (totem.getCurse() != null && totem.getCurse().isEnabled()) applyCurse(player, totem);

        for (String cmd : totem.getCommands()) {
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmd.replace("%player%", player.getName()));
        }

        for (String give : totem.getGiveItems()) {
            String[] parts = give.split(" ");
            player.getInventory().addItem(new ItemStack(Material.valueOf(parts[0].toUpperCase()), Integer.parseInt(parts[1])));
        }

        LoggerUtil.log(player.getName() + " активировал тотем: " + totem.getId());
        return true;
    }

    private void applyEffect(Player player, String effect) {
        String[] parts = effect.split(":");
        if (parts.length != 3) return;
        PotionEffectType type = PotionEffectType.getByName(parts[0].toUpperCase());
        if (type == null) return;
        player.addPotionEffect(new PotionEffect(type, Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
    }

    private void applyCurse(Player activator, CustomTotem totem) {
        int count = 0;
        for (Player target : activator.getWorld().getPlayers()) {
            if (target.equals(activator)) continue;
            if (target.getLocation().distance(activator.getLocation()) > totem.getCurse().getRadius()) continue;
            for (String eff : totem.getCurse().getEffects()) applyEffect(target, eff);
            if (!totem.getCurse().getMessage().isEmpty()) target.sendMessage(totem.getCurse().getMessage());
            count++;
        }
        if (count > 0) {
            activator.sendMessage(plugin.getMessageManager().format("curse-applied", "count", count));
            LoggerUtil.log(activator.getName() + " наложил проклятье на " + count + " игроков.");
        }
    }
}