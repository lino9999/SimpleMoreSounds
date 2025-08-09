package com.Lino.simpleMoreSounds.listeners;

import com.Lino.simpleMoreSounds.config.SoundKey;
import com.Lino.simpleMoreSounds.managers.SoundManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;

public class InventoryListener implements Listener {
    private final SoundManager soundManager;

    public InventoryListener(SoundManager soundManager) {
        this.soundManager = soundManager;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;

        Player player = (Player) event.getPlayer();
        InventoryType type = event.getInventory().getType();

        if (type == InventoryType.CRAFTING) {
            soundManager.playSound(SoundKey.INVENTORY_CLOSE, player);
        } else if (type == InventoryType.SHULKER_BOX) {
            soundManager.playSound(SoundKey.SHULKER_OPEN, player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerAttemptPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        if (player.getInventory().firstEmpty() == -1) {
            soundManager.playSound(SoundKey.INVENTORY_FULL, player);
        }
    }
}