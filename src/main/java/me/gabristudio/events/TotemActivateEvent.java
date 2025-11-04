package me.gabristudio.events;

import me.gabristudio.totems.CustomTotem;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class TotemActivateEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private final CustomTotem totem;
    private boolean cancelled = false;

    public TotemActivateEvent(Player player, CustomTotem totem) {
        super(player);
        this.totem = totem;
    }

    public CustomTotem getTotem() { return totem; }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancel) { this.cancelled = cancel; }

    @Override
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
